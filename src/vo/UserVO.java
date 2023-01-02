package vo;

public class UserVO {
	public UserVO() {;}
	
	private int userNumber;
	private String userId;
	private String userPassword;
	private String userName;
	private String userPhoneNumber;
	private int userLikeTeam;
	private int userLikePlayer;
	private int userPoint;
	
	
	public int getUserNumber() {
		return userNumber;
	}

	public void setUserNumber(int userNumber) {
		this.userNumber = userNumber;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPhoneNumber() {
		return userPhoneNumber;
	}

	public void setUserPhoneNumber(String userPhoneNumber) {
		this.userPhoneNumber = userPhoneNumber;
	}

	public int getUserLikeTeam() {
		return userLikeTeam;
	}

	public void setUserLikeTeam(int userLikeTeam) {
		this.userLikeTeam = userLikeTeam;
	}

	public int getUserLikePlayer() {
		return userLikePlayer;
	}

	public void setUserLikePlayer(int userLikePlayer) {
		this.userLikePlayer = userLikePlayer;
	}

	public int getUserPoint() {
		return userPoint;
	}

	public void setUserPoint(int userPoint) {
		this.userPoint = userPoint;
	}

	@Override
	public String toString() {
		String str = "번호 : " + userNumber + "\n" + "아이디 : " + userId + "\n" + "이름 : " + userName + "\n" + "휴대폰 번호 : " + userPhoneNumber + "\n"
				+ "좋아하는 팀: " + userLikeTeam + "\n" + "좋아하는 선수 : " + userLikePlayer + "\n" + "포인트: " + userPoint;
		return str;
	}
}