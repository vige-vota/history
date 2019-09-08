package it.vige.labs.gc.votingpapers;

public class Candidate extends Identifier {

	public String getImage() {
		return getString("image");
	}

	public void setImage(String image) {
		put("image", image);
	}

	public char getSex() {
		return (char) get("sex");
	}

	public void setSex(char sex) {
		put("sex", sex);
	}

}
