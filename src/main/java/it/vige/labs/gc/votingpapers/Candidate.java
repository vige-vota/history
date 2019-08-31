package it.vige.labs.gc.votingpapers;

public class Candidate extends Identifier {

	private String image;

	private char sex;

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		put("image", image);
		this.image = image;
	}

	public char getSex() {
		return sex;
	}

	public void setSex(char sex) {
		put("sex", sex);
		this.sex = sex;
	}

}
