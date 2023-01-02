package test;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.Scanner;

import dao.DBConnecter;
import dao.FunctionDAO;
import dao.UserDAO;
import vo.UserVO;

public class Test {
	public static void main(String[] args) {
		Connection connection = null; // DB에 연결할 connection 객체는 처음에 null로 시작
		try {connection = DBConnecter.getConnection();}
		catch (Exception e) {e.printStackTrace();} // DB에 연결 실행
		finally {
			if (connection != null) { // 만약 connection이 null이 아닌 경우 즉, 이미 연결되어 있는 경우
				try {connection.close();}
				catch (SQLException e) {throw new RuntimeException(e.getMessage());}// connection 닫음
			}
		}

		String msg = "⚽ English Premier League(EPL) ⚽" + "\n1.회원가입\n2.로그인\n3.아이디찾기\n4.비밀번호찾기\n5.종료";
		String menu = "1.오늘의 경기\n2.좋아하는 팀 경기 일정\n3.좋아하는 팀의 최다 득점자 조회\n" + "4.좋아하는 선수의 경기 일정\n5.대표 라이벌전 일정\n6.미니게임\n"
				+ "7.내 정보 조회(마이 페이지)\n8.내 정보 수정\n9.회원 탈퇴\n10.로그아웃";
		String derbyName = "일정이 궁금하신 라이벌전을 선택하세요.\n1.북런던 더비 : 토트넘 vs 아스널\n"
				+ "2.노스 웨스트 더비 : 맨유 vs 리버풀\n3.맨체스터 더비 : 맨유 vs 맨시티\n"
				+ "4.머지사이드 더비 : 리버풀 vs 에버턴\n5.로즈 더비 : 맨유 vs 리즈\n"
				+ "6.런던 더비 : 첼시 vs 아스널\n7.런던 더비 : 토트넘 vs 첼시\n8.코리안 더비 : 토트넘 vs 울버햄튼";
		
		Scanner sc = new Scanner(System.in);
		Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String today = sdf.format(calendar.getTime());
		Random r = new Random();
		int firstChoice = 0, secondChoice = 0, derbyChoice = 0;
		UserDAO userDAO = new UserDAO();
		UserVO userVO = new UserVO();
		String id = "", name = "", password = "", phoneNumber = "";
		int likeTeamNumber = 0, likePlayerNumber = 0;
		boolean run = true;
		
		
		while (true) {
			System.out.println(msg + "\n");
			System.out.print("메뉴 입력 : ");
			firstChoice = sc.nextInt();
			if (firstChoice == 5) {
				System.out.println("\n==========================");
				System.out.println("🖥프로그램을 종료합니다.🖥");
				System.out.println("==========================\n");
				break;
			}

			switch (firstChoice) {
			case 1:// 회원가입
				System.out.println("\n==========================");
				System.out.println("📑가입하실 정보를 입력해주세요📑");
				System.out.println("==========================\n");
				
				System.out.print("아이디를 입력해주세요 > ");
				id = sc.next();
				if(userDAO.checkID(id)) { // 입력한 아이디 중복검사
					System.out.println("\n이미 존재하는 아이디입니다. 다시 시도해주세요!\n");
					continue;
				}
				
				
				System.out.print("비밀번호를 입력해주세요 > ");
				password = sc.next();
				System.out.print("이름을 입력해주세요 > ");
				name = sc.next();
				
				System.out.print("핸드폰 번호를 입력해주세요 > ");
				phoneNumber = sc.next();
				
				if(!userDAO.countPhone(phoneNumber)) { // 입력한 휴대번호 가입 아이디 개수 확인
					System.out.println("\n같은 회선의 휴대폰 번호로 최대 3개까지의 계정만 가입할 수 있습니다.\n다른 번호로 다시 시도해주세요.\n");
					continue;
				}
				
				for (int i = 0; i < userDAO.printTeamList().size(); i++) {
					System.out.println(userDAO.printTeamList().get(i));
				}
				System.out.println("좋아하는 팀을 입력해주세요 > ");
				likeTeamNumber = sc.nextInt();
				
				System.out.println();
				
				for (int i = 0; i < userDAO.printPlayerList().size(); i++) {
					System.out.println(userDAO.printPlayerList().get(i));
				}
				System.out.println("좋아하는 선수을 입력해주세요 > ");
				likePlayerNumber = sc.nextInt();

				userVO.setUserId(id);
				userVO.setUserPassword(password);
				userVO.setUserName(name);
				userVO.setUserPhoneNumber(phoneNumber);
				userVO.setUserLikeTeam(likeTeamNumber);
				userVO.setUserLikePlayer(likePlayerNumber);
				userDAO.register(userVO);
				
				System.out.println("\n========================================");
				System.out.println("🎉회원가입이 완료되었습니다! 로그인을 해주세요🎉");
				System.out.println("========================================\n");
				
				break;
			case 2:// 로그인
				System.out.print("아이디를 입력해주세요 > ");
				id = sc.next();
				System.out.print("비밀번호를 입력해주세요 > ");
				password = sc.next();

				if (userDAO.login(id, password) > 0) {
					System.out.println("\n==================");
					System.out.println("🙇‍♂️‍" + userDAO.selectUser().getUserId() + "님, 환영합니다.🙇");
					System.out.println("==================\n");
					while(run) {
						// 로그인 이후
						System.out.print(menu + "\n");
						System.out.print("메뉴 입력 : ");
						secondChoice = sc.nextInt();
						
						FunctionDAO functionDAO = null;
						switch (secondChoice) {
						case 1:// 1.오늘의 경기
							System.out.println("\n============================");
							System.out.println("🎉" + today + " 오늘의 경기 일정!🎉");
							System.out.println("============================\n");
							functionDAO = new FunctionDAO();
							functionDAO.todaySoccerGame();
							break;
							
						case 2:// 2.좋아하는 팀 경기 일정
							System.out.println(userDAO.findTeamName());
							
							System.out.println("\n=================================");
							System.out.println("🎉" + userDAO.findTeamName() + "  팀의 경기 일정을 조회합니다...🎉");
							System.out.println("=================================\n");
							
							functionDAO = new FunctionDAO();
							System.out.println("\n=================================");
							System.out.println("🎉" + userDAO.findTeamName() + "  팀의 경기 일정!🎉");
							System.out.println("=================================\n");
							System.out.println(functionDAO.gameSchedule(userDAO.findTeamName()));
							break;
							
						case 3:// 3.좋아하는 팀의 최다 득점자 조회
							System.out.println("\n=================================");
							System.out.println("🎉" + userDAO.findTeamName() + "  팀에서 최다 득점자를 조회합니다...🎉");
							System.out.println("=================================\n");
							functionDAO = new FunctionDAO();
							System.out.println("\n=================================");
							System.out.println("🎉" + userDAO.findTeamName() + "  팀의 최다 득점자!🎉");
							System.out.println("=================================\n");
							System.out.println(functionDAO.getHighestScorer(userDAO.findTeamName()));
							break;
							
						case 4:// 4.좋아하는 선수의 경기 일정
							System.out.println("\n=================================");
							System.out.println("🎉" + userDAO.findPlayerInfo(userDAO.findPlayerNumber()) + "  선수 소속팀의 경기 일정을 조회합니다...🎉");
							System.out.println("=================================\n");
							functionDAO = new FunctionDAO();
							System.out.println("\n=================================");
							System.out.println("🎉" + userDAO.findPlayerInfo(userDAO.findPlayerNumber()) + "  선수 소속팀의 경기 일정!🎉");
							System.out.println("=================================\n");
							System.out.println(functionDAO.gameSchedule(userDAO.findPlayerInfo())); // 좋아하는 선수가 속한 팀명을 매개변수로 넣기
							break;
							
						case 5:// 5.대표 라이벌전 일정
							System.out.println("\n=================================");
							System.out.println(derbyName);
							System.out.println("=================================");
							derbyChoice = sc.nextInt();
							if(derbyChoice > 0 && derbyChoice <= 8) {
								functionDAO = new FunctionDAO();
								System.out.println("라이벌 경기 찾는 중...");
								System.out.println("=================================");
								System.out.print(functionDAO.derbySchedule(derbyChoice));
								System.out.println("=================================\n");
							}
							break;
							
						case 6:// 6.미니게임
							if(userDAO.selectUser().getUserPoint() <= 0) {
								System.out.println("\n=================================");
								System.out.println("게임을..진행하기에는...포인트가..부족..합니다...");
								System.out.println("잔여 포인트 : " + userDAO.selectUser().getUserPoint());
								System.out.println(("=================================\n"));
								break;
							}
							System.out.println("\n=================================");
							System.out.println("랜덤한 경기 일정을 조회합니다...");
							System.out.println(("================================="));
							functionDAO = new FunctionDAO();
							String result = functionDAO.miniGame();
							
							String[] resultArr = result.split(",");
							
							String matchDate = resultArr[0]; // 경기 날짜
							String homeTeam = resultArr[1]; // 홈팀 이름
							int homeScore = Integer.parseInt(resultArr[2]); // 홈팀 점수
							int awayScore = Integer.parseInt(resultArr[3]); // 어웨이팀 점수
							String awayTeam = resultArr[4]; // 어웨이팀 이름
							
							int gameResult = 0;
							System.out.println("\n=================================");
							System.out.println("⚽게임을 시작합니다!⚽");
							System.out.println("=================================");
							
							System.out.println("경기한 날짜 -> " + matchDate);
							System.out.println("경기한 팀 -> " + homeTeam + " : " + awayTeam + "\n");
							System.out.println(1 + ". " + homeTeam);
							System.out.println(2 + ". " + awayTeam);
							System.out.println(3 + ". 무승부");
							System.out.println("=================================");
							
							if(homeScore > awayScore) { // 홈팀 스코어가 더 높을 경우
								gameResult = 1;
							} else if(homeScore == awayScore) {
								gameResult = 3;
							} else { // 어웨이팀 스코어가 더 높을 경우
								gameResult = 2;
							}
							
							System.out.println("경기결과를 번호를 통해 선택해주세요.(1~3)");
							System.out.print("정답 : ");
							int answer = sc.nextInt();
							
							System.out.println("==============결과=================");
							if(answer == gameResult) {
								System.out.println("정답입니다!");
								userDAO.gameWin();
							} else {
								System.out.println("오답입니다.");
								userDAO.gameLose();
							}
							System.out.println("경기 결과 -> " + homeTeam + " " + homeScore + " : " + awayScore + " " + awayTeam);
							System.out.println("잔여 포인트 : " + userDAO.selectUser().getUserPoint());
							System.out.println("=================================\n");
							break;
							
						case 7:// 7.내 정보 조회(마이 페이지)
							System.out.println("\n=================================");
							System.out.println("🎉" + userDAO.selectUser().getUserId() + "님의 회원정보🎉");
							System.out.println("회원번호 : " + userDAO.selectUser().getUserNumber());
							System.out.println("아이디 : " + userDAO.selectUser().getUserId());
							System.out.println("이름 : " + userDAO.selectUser().getUserName());
							System.out.println("핸드폰 번호 : " + userDAO.selectUser().getUserPhoneNumber());
							System.out.println("좋아하는 팀 : " + userDAO.findTeamName());
							System.out.println("좋아하는 선수 : " + userDAO.findPlayerInfo((userDAO.selectUser().getUserLikePlayer())));
							System.out.println("포인트 : " + userDAO.selectUser().getUserPoint());
							System.out.println("=================================\n");
							break;
							
						case 8:// 8.내 정보 수정
							System.out.println("\n=================================");
							System.out.println("🎉" + userDAO.selectUser().getUserId() + "님의 회원정보🎉");
							
							System.out.print("변경할 비밀번호를 입력해주세요 > ");
							password = sc.next();
							System.out.print("변경할 이름을 입력해주세요 > ");
							name = sc.next();
							System.out.print("변경할 핸드폰 번호를 입력해주세요 > ");
							phoneNumber = sc.next();
							
							if(!userDAO.countPhone(phoneNumber)) {
								System.out.println("이미 중복된 핸드폰 번호가 3개 있습니다. 다른 번호로 처음부터 다시 시도해주세요.");
								break;
							}
							
							for (int i = 0; i < userDAO.printTeamList().size(); i++) {
								System.out.println(userDAO.printTeamList().get(i));
							}
							System.out.println("변경하실 최애팀을 입력해주세요 > ");
							likeTeamNumber = sc.nextInt();
							
							System.out.println();
							
							for (int i = 0; i < userDAO.printPlayerList().size(); i++) {
								System.out.println(userDAO.printPlayerList().get(i));
							}
							System.out.println("변경하실 최애선수을 입력해주세요 > ");
							likePlayerNumber = sc.nextInt();
							
							userVO.setUserPassword(password);
							userVO.setUserName(name);
							userVO.setUserPhoneNumber(phoneNumber);
							userVO.setUserLikeTeam(likeTeamNumber);
							userVO.setUserLikePlayer(likePlayerNumber);
							userDAO.updateUser(userVO);
							System.out.println("\n=================================");
							System.out.println("정보수정 완료!");
							System.out.println("=================================\n");
							break;
							
						case 9:// 9.회원 탈퇴
							System.out.println("\n=================================");
							System.out.println("탈퇴를 진행합니다.");
							System.out.println("=================================");
							System.out.println("현재 사용하고 계시는 비밀번호를 입력하세요.");
							String pw = sc.next();
							
							System.out.println("\n==============결과=================");
							if(userDAO.deleteUser(pw)) {
								System.out.println("탈퇴 완료");
							} else {
								System.out.println("비밀번호를 다시 확인해주세요.");
							}
							System.out.println("=================================\n");
							run = false;
							break;
						case 10: //10.로그아웃
							System.out.println("\n=============================");
							System.out.println("🙇‍♂️‍" + userDAO.selectUser().getUserId() + "님, 다음에 다시 뵙겠습니다.🙇");
							System.out.println("=============================\n");
							userDAO.logOut();
							run = false;
							break;
						default:
							break;
						}
					}
				} else {
					System.out.println("\n=================================");
					System.out.println("아이디와 비밀번호를 다시 확인해주세요.");
					System.out.println("=================================");
				}
				break;
			case 3:// 아이디찾기
				System.out.println("\n==========================");
				System.out.println("📑가입하신 정보를 입력해주세요📑");
				System.out.println("==========================\n");
				
				System.out.println("가입하신 성함을 입력해주세요 > ");
				name = sc.next();
				System.out.println("가입하실 때 입력하신 휴대폰 번호를 입력해주세요.");
				phoneNumber = sc.next();
				ArrayList<String> idList = userDAO.findId(name, phoneNumber); 
				
				// 아이디가 없는 경우
				if (idList.size() == 0) {
					System.out.println("\n==========================");
					System.out.println("존재하는 회원 아이디를 찾을 수 없습니다. 성함과 휴대폰 번호를 다시 확인해주세요.");
					System.out.println("==========================\n");
					continue;
				}
				
				System.out.println("\n====입력하신 정보로 가입된 아이디 목록====");
				for (int i = 0; i < idList.size(); i++) {
					System.out.println((i+1) + ". " + idList.get(i));
				}
				System.out.println();
				break;
				
			case 4:// 비밀번호찾기
				System.out.println("\n==========================");
				System.out.println("📑가입하신 정보를 입력해주세요📑");
				System.out.println("==========================\n");
				
				System.out.println("가입하신 아이디를 입력해주세요.");
				id = sc.next();
				System.out.println("가입하실 때 입력하신 휴대폰 번호를 입력해주세요.");
				phoneNumber = sc.next();
				
				if(userDAO.findPassword(id, phoneNumber)) {
					System.out.println("비밀번호 변경을 진행합니다.");
					System.out.println("변경하실 비밀번호를 입력해주세요.");
					password = sc.next();
					userDAO.changePassword(password);
					System.out.println("\n==============결과=================");
					System.out.println("비밀번호 변경이 완료되었습니다!");	
					System.out.println("=================================\n");
				} else {
					System.out.println("\n==============결과=================");
					System.out.println("아이디 혹은 휴대폰 번호가 존재하지 않습니다.");
					System.out.println("=================================\n");
				}
				break;
			default:
				System.out.println("\n==============결과=================");
				System.out.println("잘 못 입력하셨습니다. 다시 입력해주세요.");
				System.out.println("=================================\n");
				break;
			}
		}
	}
}