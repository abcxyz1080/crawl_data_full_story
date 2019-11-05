package com.example.fullstory.controller;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fullstory.model.Category;
import com.example.fullstory.model.Chapter;
import com.example.fullstory.model.Commic;
import com.example.fullstory.repository.CategoryRepository;
import com.example.fullstory.repository.ChapterRepository;
import com.example.fullstory.repository.CommicRepository;

@RestController
@RequestMapping("/api")

public class CrawlerController {

	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	CommicRepository commicRepository;
	@Autowired
	ChapterRepository chapterRepository;

	public Set<Category> getListCategory() throws IOException {
		Set<Category> categories = categoryRepository.findAll().stream().collect(Collectors.toSet());

		Document document = Jsoup.connect("https://truyenfull.vn/").get();
		Elements elements = document.select("div.row > div.col-md-4 > ul > li > a");
		for (Element element : elements) {
			Category newCategory = new Category();
			String[] temp = (element.attr("href").split("/"));
			String name = element.text();
			String urlName = temp[temp.length - 1];
			newCategory.setName(name);
			newCategory.setLink(urlName);
			categories.add(newCategory);
		}

		categoryRepository.saveAll(categories);
		return categories;
	}

	@GetMapping("/crawler")
	public boolean crawlCommic() throws IOException {
		Set<Category> categories = getListCategory();
		int pageComic = Integer.parseInt(Jsoup.connect("https://truyenfull.vn/danh-sach/truyen-hot/").get()
				.select("div.container.text-center.pagination-container > div > ul > li:nth-child(8) > a").first()
				.attr("title").replaceAll("[^0-9?!\\.]", ""));
		// System.out.println(pageComic);
		for (int i = 1; i < 2; i++) {
			System.out.println("Page: " + i);
			Document document = Jsoup.connect("https://truyenfull.vn/danh-sach/truyen-hot/trang-" + i + "/").get();
			Elements elements = document.select(
					"#list-page > div.col-xs-12.col-sm-12.col-md-9.col-truyen-main > div.list.list-truyen.col-xs-12 > div.row");
			for (Element element : elements) {
				Document docStoryDetail = Jsoup.connect(element.select("h3.truyen-title > a").attr("href")).get();
				Elements categoriesStory = docStoryDetail.select("div:nth-child(2) > a");

				String title = element.select("h3.truyen-title > a").text();
				String description = docStoryDetail.select("div.col-xs-12.col-sm-8.col-md-8.desc > div.desc-text")
						.first().text();
				String author = element.select("span.author").text();
				String thumbnail = element.select("div.col-xs-3 > div > div.lazyimg").attr("data-image");
				String status = docStoryDetail.select("div.info > div > span").last().text();
				String numberOfChapter = element.select("div.col-xs-2 > div > a").text();

				// Add comic to database
				Commic commicModel = new Commic();
				commicModel.setTitle(title);
				commicModel.setDescription(description);
				commicModel.setAuthor(author);
				commicModel.setThumbnail(thumbnail);
				commicModel.setStatus(status);
				commicModel.setLink(element.select("h3.truyen-title > a").attr("href"));
				commicModel.setNumberOfChapter(numberOfChapter);
				for (Element category : categoriesStory) {
					for (Category cate : categories) {
						if (category.text().equals(cate.getName())) {
							// commicModel.getCategories().add(cate);
							cate.getCommics().add(commicModel);
						}
					}
				}

				// Crawl commic again when chapter new

				if (commicRepository.findByTitle(title) == null) {
					// System.out.println("Commic chưa có trong db !");
					commicRepository.save(commicModel);
					crawlChapterOfCommic(element.select("h3.truyen-title > a").attr("href"), commicModel);
				} else {
					Commic oldCommic = commicRepository.findByTitle(title);
					if (!oldCommic.getStatus().equals("Full")) {
						if (!oldCommic.getNumberOfChapter().equals(numberOfChapter)) {
							// System.out.println("Commic đã có trong db và có chương mới");
							commicRepository.updateNumberOfChapter(numberOfChapter, title);
							crawlChapterOfCommic(element.select("h3.truyen-title > a").attr("href"), commicModel);
						} /*
							 * else { System.err.println("Commic đã có trong db và chưa có chương mới! ");
							 * // }
							 */
					}
				}
			}
		}

		return true;
	}

	public void crawlChapterOfCommic(String commicUrl, Commic commic) throws IOException {
		Set<Chapter> chapters = chapterRepository.findAll().stream().collect(Collectors.toSet());
		boolean hasNext = false;
		do {
			Document document = Jsoup.connect(commicUrl).get();
			Elements elements = document.select("#list-chapter > div.row > div > ul > li > a");
			for (Element element : elements) {
				Document docChapterDetail = Jsoup.connect(element.attr("href")).get();
				Element content = docChapterDetail.select("#chapter-c").first();

				Chapter chapterModel = new Chapter();
				chapterModel.setTitle(element.attr("title"));
				chapterModel.setContent(content.text());
				chapterModel.setLink(element.attr("href"));
				chapterModel.setCommic(commic);
				chapters.add(chapterModel);
			}
			chapterRepository.saveAll(chapters);
			Element nextPageButton = document.select("#list-chapter > ul > li.active + li > a").first();
			commicUrl = nextPageButton != null ? nextPageButton.attr("href") : "javascript:void(0)";
			if (!commicUrl.equals("javascript:void(0)")) {
				hasNext = true;
				commicUrl = nextPageButton.attr("href");
				// System.out.println("Chapter link:" + commicUrl);
			} else {
				hasNext = false;
			}
		} while (hasNext);
	}
}
