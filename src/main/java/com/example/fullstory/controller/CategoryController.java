
package com.example.fullstory.controller;

import static com.example.fullstory.util.ResponseUtil.returnListCategory;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fullstory.model.Category;
import com.example.fullstory.repository.CategoryRepository;

@RestController
@RequestMapping("/api")
public class CategoryController {

	@Autowired
	CategoryRepository categoryRepository;

	@GetMapping(value = "/category", produces = "application/json")
	public String GetAllCrawler() throws IOException {
		return returnListCategory(categoryRepository.findAll()).toString();
	}

	@PostMapping("/category")
	public String CreateCategoryCrawler() throws IOException {
		Document doc = Jsoup.connect("https://truyenfull.vn/").get();
		Elements categories = doc.select("div.row > div.col-md-4 > ul > li > a");

		for (Element category : categories) {
			Category categoryModel = new Category();
			categoryModel.setName(category.text());
			categoryModel.setLink(category.attr("href"));

			// Check category duplicated
			boolean checkDuplicated = false;
			checkDuplicated = categoryRepository.findAll().stream()
					.anyMatch((s) -> s.getName().equals(category.text()));
			if (!checkDuplicated) {
				categoryRepository.save(categoryModel);
			} else {
				System.err.println("Story " + category.text() + " đã có trong db!!");
			}

		}
		return "Thêm category thành công!!";
	}
}
