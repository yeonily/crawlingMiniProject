package dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class FunctionDAO {

	private static WebDriver webDriver;
	private String url;
	public static final String WEB_DRIVER_PATH = "C:/chromedriver.exe";
	public static final String WEB_DRIVER_ID = "webdriver.chrome.driver";

	// 기본생성자
	public FunctionDAO() {
		System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
		url = "https://sports.daum.net/schedule/epl";
		ChromeOptions options = new ChromeOptions();
//        options.addArguments("headless");

		webDriver = new ChromeDriver(options);
		webDriver.get(url);

	}

	// 1번 ; 오늘의 경기 > 오늘 날짜인 경기 정보 크롤링해서 출력
	public void todaySoccerGame() {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat today = new SimpleDateFormat("yyyyMMdd");
		String todaySoccer = today.format(calendar.getTime());
//         String todaySoccer = "20220901";

		ArrayList<String> date = new ArrayList<String>();
		ArrayList<String> time = new ArrayList<String>();
		ArrayList<String> place = new ArrayList<String>();
		ArrayList<String> leftTeamName = new ArrayList<String>();
		ArrayList<String> rightTeamName = new ArrayList<String>();
		for (WebElement webElement : webDriver.findElements(By.cssSelector("tbody tr"))) {
			try {
				webElement.findElement(By.cssSelector("td.td_empty"));
			} catch (NoSuchElementException e) {
				date.add(webElement.getAttribute("data-date"));
			}
		}

		for (WebElement webElement : webDriver.findElements(By.cssSelector("td.td_time"))) {
			time.add(webElement.getText());
		}

		for (WebElement webElement : webDriver.findElements(By.cssSelector("td.td_area"))) {
			place.add(webElement.getText());
		}

		for (WebElement webElement : webDriver.findElements(By.cssSelector("div.team_home span.txt_team "))) {
			leftTeamName.add(webElement.getText());
		}

		for (WebElement webElement : webDriver.findElements(By.cssSelector("div.team_away span.txt_team"))) {
			rightTeamName.add(webElement.getText());
		}

		if (date.contains(todaySoccer)) {
			System.out.println("------------------------------ 오늘의 경기 ------------------------------");
			for (int i = 0; i < date.size(); i++) {
				if (todaySoccer.equals(date.get(i))) {
					System.out.println(date.get(i) + " >> " + time.get(i) + "   장소 : " + place.get(i) + "     "
							+ leftTeamName.get(i) + "  VS  " + rightTeamName.get(i));
				}
			}
		} else {
			System.out.println("오늘은 경기 일정이 없습니다.");
		}

		webDriver.quit();
	}

	// 2번, 4번 ; 팀 경기 일정
	public String gameSchedule(String teamName) {
		int i = 0;
		String result = "";

		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat today = new SimpleDateFormat("yyyyMMdd");
		String todaySoccer = today.format(calendar.getTime());
		ArrayList<String> dates = new ArrayList<String>();
		ArrayList<String> times = new ArrayList<String>();
		ArrayList<String> stadiums = new ArrayList<String>();
		ArrayList<String> homeTeams = new ArrayList<String>();
		ArrayList<String> awayTeams = new ArrayList<String>();
		ArrayList<String> homeScores = new ArrayList<String>();
		ArrayList<String> awayScores = new ArrayList<String>();

		for (int j = 2; j < 12; j++) {
			webDriver.findElement(By.cssSelector("div.info_month a:nth-child(" + j + ")")).click();
//                try {Thread.sleep(300);} catch (InterruptedException e) {;}

			for (WebElement webElement : webDriver.findElements(By.cssSelector("tbody tr"))) {
				try {
					webElement.findElement(By.cssSelector("td.td_empty"));
				} catch (NoSuchElementException e) {
					dates.add(webElement.getAttribute("data-date"));
				}
			}

			for (WebElement webElement : webDriver.findElements(By.cssSelector("td.td_time"))) {
				times.add(webElement.getText());
			}

			for (WebElement webElement : webDriver.findElements(By.cssSelector("td.td_area"))) {
				stadiums.add(webElement.getText());
			}

			for (WebElement webElement : webDriver.findElements(By.cssSelector("div.team_home span.txt_team"))) {
				homeTeams.add(webElement.getText());

			}

			for (WebElement webElement : webDriver.findElements(By.cssSelector("div.team_away span.txt_team"))) {
				awayTeams.add(webElement.getText());
			}

			for (WebElement webElement : webDriver.findElements(By.cssSelector("div.team_home em.num_score"))) {
				homeScores.add(webElement.getText());
			}

			for (WebElement webElement : webDriver.findElements(By.cssSelector("div.team_away em.num_score"))) {
				awayScores.add(webElement.getText());
			}
		}
		for (int j = 0; j < homeTeams.size(); j++) {
			if (teamName.equals(homeTeams.get(j)) || teamName.equals(awayTeams.get(j))) {
				result += ++i + ". 날짜 : " + dates.get(j) + " / 시간 : " + times.get(j) + " / 구장 : " + stadiums.get(j)
						+ " / " + homeTeams.get(j) + " " + homeScores.get(j) + " : " + awayScores.get(j) + " "
						+ awayTeams.get(j) + "\n";
			}
		}

		webDriver.quit();
		return result;
	}

	// 3번 ; 좋아하는 팀의 최다 득점자 크롤링
	public String getHighestScorer(String favoriteTeam) {
		String str = "";
		int max = 0;

		List<WebElement> teamNames = null;
		List<WebElement> goalCounts = null;
		List<WebElement> playerNames = null;
		ArrayList<String> playerName = new ArrayList<String>();
		ArrayList<String> teamName = new ArrayList<String>();
		ArrayList<Integer> goalCount = new ArrayList<Integer>();
		ArrayList<String> selectedTeam = new ArrayList<String>();
		ArrayList<String> selectedPlayer = new ArrayList<String>();
		ArrayList<Integer> selectedGoal = new ArrayList<Integer>();
		webDriver.findElement(By.cssSelector("div.inner_gnb ul.subgnb_comm li:nth-child(4) a.link_subgnb")).click();
//      webDriver.findElement(By.cssSelector("li.on a.link_subgnb")).click();
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			;
		}

		webDriver.findElement(By.cssSelector("div.tab_menu2 ul.list_tab li:nth-child(2) a.link_tab")).click();
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			;
		}
		playerNames = webDriver.findElements(By.cssSelector("table.tbl_record span.txt_name"));
		teamNames = webDriver.findElements(By.cssSelector("table.tbl_record a.link_txt"));
		goalCounts = webDriver.findElements(By.cssSelector("table.tbl_record td.selected_on"));

		for (WebElement webElement : playerNames) {
			playerName.add(webElement.getText());
		}

		for (WebElement webElement : teamNames) {
			teamName.add(webElement.getText());
		}

		for (WebElement webElement : goalCounts) {
			goalCount.add(Integer.valueOf(webElement.getText()));
		}

		for (int i = 0; i < teamNames.size(); i++) {
//          System.out.println(teamName.get(i) + ", " + playerName.get(i) + ", " + goalCount.get(i));
			if (favoriteTeam.equals(teamName.get(i))) {
				selectedTeam.add(teamName.get(i));
				selectedPlayer.add(playerName.get(i));
				selectedGoal.add(goalCount.get(i));
			}
		}

//      System.out.println(selectedTeam+"\n"+selectedPlayer+"\n"+selectedGoal);
		for (int i = 0; i < selectedGoal.size(); i++) {
			if (max < selectedGoal.get(i)) {
				max = selectedGoal.get(i);
//              System.out.println(max);
			}
		}

		for (int i = 0; i < selectedGoal.size(); i++) {
			if (max == selectedGoal.get(i)) {
				str += selectedTeam.get(i) + "의 최다 득점자 : " + selectedPlayer.get(i) + ", 득점 수 : " + selectedGoal.get(i)
						+ "\n";
			}
		}
		webDriver.quit();
		return str;
	}

	// 5번 ; 라이벌전
	public String derbySchedule(int derbyNumber) {
		int i = 0;
		String result = "";
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat today = new SimpleDateFormat("yyyyMMdd");
		String todaySoccer = today.format(calendar.getTime());
		ArrayList<String> dates = new ArrayList<String>();
		ArrayList<String> times = new ArrayList<String>();
		ArrayList<String> stadiums = new ArrayList<String>();
		ArrayList<String> homeTeams = new ArrayList<String>();
		ArrayList<String> awayTeams = new ArrayList<String>();
		ArrayList<String> homeScores = new ArrayList<String>();
		ArrayList<String> awayScores = new ArrayList<String>();
		for (int g = 2; g < 12; g++) {
			webDriver.findElement(By.cssSelector("div.info_month a:nth-child(" + g + ")")).click();
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				;
			}
			for (WebElement webElement : webDriver.findElements(By.cssSelector("tbody tr"))) {
				try {
					webElement.findElement(By.cssSelector("td.td_empty"));
				} catch (NoSuchElementException e) {
					dates.add(webElement.getAttribute("data-date"));
				}
			}
			for (WebElement webElement : webDriver.findElements(By.cssSelector("td.td_time"))) {
				times.add(webElement.getText());
			}
			for (WebElement webElement : webDriver.findElements(By.cssSelector("td.td_area"))) {
				stadiums.add(webElement.getText());
			}
			for (WebElement webElement : webDriver.findElements(By.cssSelector("div.team_home span.txt_team"))) {
				homeTeams.add(webElement.getText());
			}
			for (WebElement webElement : webDriver.findElements(By.cssSelector("div.team_away span.txt_team"))) {
				awayTeams.add(webElement.getText());
			}
			for (WebElement webElement : webDriver.findElements(By.cssSelector("div.team_home em.num_score"))) {
				homeScores.add(webElement.getText());
			}
			for (WebElement webElement : webDriver.findElements(By.cssSelector("div.team_away em.num_score"))) {
				awayScores.add(webElement.getText());
			}
		}

		while (true) {
			switch (derbyNumber) {
			case 1:
				for (int j = 0; j < homeTeams.size(); j++) {
					if (("토트넘".equals(homeTeams.get(j)) && "아스널".equals(awayTeams.get(j)))
							|| ("아스널".equals(homeTeams.get(j)) && "토트넘".equals(awayTeams.get(j)))) {
						result += ++i + ". 날짜 : " + dates.get(j) + " / 시간 : " + times.get(j) + " / 구장 : "
								+ stadiums.get(j) + " / " + homeTeams.get(j) + " " + homeScores.get(j) + " : "
								+ awayScores.get(j) + " " + awayTeams.get(j) + "\n";
					}
				}
				break;
			case 2:
				for (int j = 0; j < homeTeams.size(); j++) {
					if (("맨유".equals(homeTeams.get(j)) && "리버풀".equals(awayTeams.get(j)))
							|| ("리버풀".equals(homeTeams.get(j)) && "맨유".equals(awayTeams.get(j)))) {
						result += ++i + ". 날짜 : " + dates.get(j) + " / 시간 : " + times.get(j) + " / 구장 : "
								+ stadiums.get(j) + " / " + homeTeams.get(j) + " " + homeScores.get(j) + " : "
								+ awayScores.get(j) + " " + awayTeams.get(j) + "\n";
					}
				}
				break;
			case 3:
				for (int j = 0; j < homeTeams.size(); j++) {
					if (("맨유".equals(homeTeams.get(j)) && "맨시티".equals(awayTeams.get(j)))
							|| ("맨시티".equals(homeTeams.get(j)) && "맨유".equals(awayTeams.get(j)))) {
						result += ++i + ". 날짜 : " + dates.get(j) + " / 시간 : " + times.get(j) + " / 구장 : "
								+ stadiums.get(j) + " / " + homeTeams.get(j) + " " + homeScores.get(j) + " : "
								+ awayScores.get(j) + " " + awayTeams.get(j) + "\n";
					}
				}
				break;
			case 4:
				for (int j = 0; j < homeTeams.size(); j++) {
					if (("리버풀".equals(homeTeams.get(j)) && "에버턴".equals(awayTeams.get(j)))
							|| ("에버턴".equals(homeTeams.get(j)) && "리버풀".equals(awayTeams.get(j)))) {
						result += ++i + ". 날짜 : " + dates.get(j) + " / 시간 : " + times.get(j) + " / 구장 : "
								+ stadiums.get(j) + " / " + homeTeams.get(j) + " " + homeScores.get(j) + " : "
								+ awayScores.get(j) + " " + awayTeams.get(j) + "\n";
					}
				}
				break;
			case 5:
				for (int j = 0; j < homeTeams.size(); j++) {
					if (("맨유".equals(homeTeams.get(j)) && "리즈".equals(awayTeams.get(j)))
							|| ("리즈".equals(homeTeams.get(j)) && "맨유".equals(awayTeams.get(j)))) {
						result += ++i + ". 날짜 : " + dates.get(j) + " / 시간 : " + times.get(j) + " / 구장 : "
								+ stadiums.get(j) + " / " + homeTeams.get(j) + " " + homeScores.get(j) + " : "
								+ awayScores.get(j) + " " + awayTeams.get(j) + "\n";
					}
				}
				break;
			case 6:
				for (int j = 0; j < homeTeams.size(); j++) {
					if (("첼시".equals(homeTeams.get(j)) && "아스널".equals(awayTeams.get(j)))
							|| ("아스널".equals(homeTeams.get(j)) && "첼시".equals(awayTeams.get(j)))) {
						result += ++i + ". 날짜 : " + dates.get(j) + " / 시간 : " + times.get(j) + " / 구장 : "
								+ stadiums.get(j) + " / " + homeTeams.get(j) + " " + homeScores.get(j) + " : "
								+ awayScores.get(j) + " " + awayTeams.get(j) + "\n";
					}
				}
				break;
			case 7:
				for (int j = 0; j < homeTeams.size(); j++) {
					if (("토트넘".equals(homeTeams.get(j)) && "첼시".equals(awayTeams.get(j)))
							|| ("첼시".equals(homeTeams.get(j)) && "토트넘".equals(awayTeams.get(j)))) {
						result += ++i + ". 날짜 : " + dates.get(j) + " / 시간 : " + times.get(j) + " / 구장 : "
								+ stadiums.get(j) + " / " + homeTeams.get(j) + " " + homeScores.get(j) + " : "
								+ awayScores.get(j) + " " + awayTeams.get(j) + "\n";
					}
				}
				break;
			case 8:
				for (int j = 0; j < homeTeams.size(); j++) {
					if (("토트넘".equals(homeTeams.get(j)) && "울버햄튼".equals(awayTeams.get(j)))
							|| ("울버햄튼".equals(homeTeams.get(j)) && "토트넘".equals(awayTeams.get(j)))) {
						result += ++i + ". 날짜 : " + dates.get(j) + " / 시간 : " + times.get(j) + " / 구장 : "
								+ stadiums.get(j) + " / " + homeTeams.get(j) + " " + homeScores.get(j) + " : "
								+ awayScores.get(j) + " " + awayTeams.get(j) + "\n";
					}
				}
				break;
			}
			return result;
		}
	}

//    -------------------------7번
	// 미니게임
	public String miniGame() {
		int i = 0;
		ArrayList<String> results = new ArrayList<String>();

		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat today = new SimpleDateFormat("yyyyMMdd");
		String todaySoccer = today.format(calendar.getTime());
		ArrayList<String> dates = new ArrayList<String>();
		ArrayList<String> times = new ArrayList<String>();
		ArrayList<String> stadiums = new ArrayList<String>();
		ArrayList<String> homeTeams = new ArrayList<String>();
		ArrayList<String> awayTeams = new ArrayList<String>();
		ArrayList<String> homeScores = new ArrayList<String>();
		ArrayList<String> awayScores = new ArrayList<String>();

		for (int j = 2; j < 4; j++) {
			webDriver.findElement(By.cssSelector("div.info_month a:nth-child(" + j + ")")).click();
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				;
			}

			for (WebElement webElement : webDriver.findElements(By.cssSelector("tbody tr"))) {
				try {
					webElement.findElement(By.cssSelector("td.td_empty"));
				} catch (NoSuchElementException e) {
					dates.add(webElement.getAttribute("data-date"));
				}
			}

			for (WebElement webElement : webDriver.findElements(By.cssSelector("td.td_time"))) {
				times.add(webElement.getText());
			}

			for (WebElement webElement : webDriver.findElements(By.cssSelector("td.td_area"))) {
				stadiums.add(webElement.getText());
			}

			for (WebElement webElement : webDriver.findElements(By.cssSelector("div.team_home span.txt_team"))) {
				homeTeams.add(webElement.getText());

			}

			for (WebElement webElement : webDriver.findElements(By.cssSelector("div.team_away span.txt_team"))) {
				awayTeams.add(webElement.getText());
			}

			for (WebElement webElement : webDriver.findElements(By.cssSelector("div.team_home em.num_score"))) {
				homeScores.add(webElement.getText());
			}

			for (WebElement webElement : webDriver.findElements(By.cssSelector("div.team_away em.num_score"))) {
				awayScores.add(webElement.getText());
			}
		}
		for (int j = 0; j < homeTeams.size(); j++) {
			if (!homeScores.get(j).equals("-")) {
				results.add(dates.get(j) + "," + homeTeams.get(j) + "," + homeScores.get(j) + "," + awayScores.get(j)
						+ "," + awayTeams.get(j));
			}
		}
		Random r = new Random();
		int ranNum = r.nextInt(results.size());

		webDriver.quit();
		return results.get(ranNum);
	}
}