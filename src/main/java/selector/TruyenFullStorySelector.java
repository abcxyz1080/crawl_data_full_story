package selector;

public class TruyenFullStorySelector {
	public String title() {
		return "h3[class=title] [itemprop=name]";
	}

	public String author() {
		return "a[itemprop=author]";
	}

	public String category() {
		return ".info a[]itemprop=genre";
	}

	public String description() {
		return ".desc-text";
	}

	public String image() {
		return "img[itemprop=image]";
	}
}
