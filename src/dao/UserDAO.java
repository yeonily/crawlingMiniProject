package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import vo.UserVO;

public class UserDAO {
	public Connection connection;
	public PreparedStatement preparedStatement;
	public ResultSet resultSet;
	
	
	public static int loginStatus = 0; // 현재 로그인된 회원번호를 저장하기 위한 전역변수
	private static int passwordCheck = 0; // 아이디와 휴대폰 번호를 참고하여 회원정보가 일치할 경우 해당 회원번호를 저장
	
	/* 회원가입 */
	//	1 = 가입 성공, -1 = 이미 존재하는 아이디, -2 = 휴대폰 가입 개수 초과
	public int register(UserVO userVO) {
		String query = "insert into tbl_user "
				+ "(userId, userPassword, userName, userPhoneNumber, userLikeTeam, userLikePlayer) " + "values(?, ?, ?, ?, ?, ?)";
		
		/* 메인 메소드에서 사용 */
		if(checkID(userVO.getUserId())) {return -1;} // 테이블에서 중복되는 아이디를 찾아 회원번호를 저장한 경우
		if(!countPhone(userVO.getUserPhoneNumber())) { return -2; } // 가입하려는 핸드폰번호가 DB에 이미 3개가 있는 경우 가입 불가
		
		try {
			connection = DBConnecter.getConnection();
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, userVO.getUserId());
			preparedStatement.setString(2, userVO.getUserPassword());
			preparedStatement.setString(3, userVO.getUserName());
			preparedStatement.setString(4, userVO.getUserPhoneNumber());
			preparedStatement.setInt(5, userVO.getUserLikeTeam());
			preparedStatement.setInt(6, userVO.getUserLikePlayer());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {System.out.println("회원가입 쿼리문 오류");}
		finally {
			try {
				if (preparedStatement != null) {preparedStatement.close();}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {throw new RuntimeException(e.getMessage());}
		}
		return 1;
	}
	
	/* 아이디 중복검사 */
	public boolean checkID(String userId) {
		String query = "select userNumber from tbl_user where userId = ?";
		
		try {
			connection = DBConnecter.getConnection();
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, userId);
			resultSet = preparedStatement.executeQuery();
			if(resultSet.next()) {return true;} // 입력받은 아이디가 테이블에 존재 -> true
			
		} catch (SQLException e) {System.out.println("아이디 중복검사 쿼리문 오류");}
		finally {
			try {
				if (resultSet != null) {resultSet.close();}
				if (preparedStatement != null) {preparedStatement.close();}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {throw new RuntimeException(e.getMessage());}
		}
		return false; // 테이블에 존재하지 않으면 false
	}
	
	/* 회선 가입 수 */
	// 매개변수로 가입하려는 유저의 핸드폰 번호를 입력받아 이미 3개가 가입되어 있는 경우 회원가입 진행 불가
	public boolean countPhone(String userPhoneNumber) {  
		int cntPhone = 0; // 매개 변수로 받은 핸드폰 번호로 가입된 아이디의 개수를 저장할 변수
		String query = "select count(userPhoneNumber) from tbl_user where userPhoneNumber = ?"; // 입력한 핸드폰 번호의 개수를 조회
		
		try {
			connection = DBConnecter.getConnection();
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, userPhoneNumber);
			resultSet = preparedStatement.executeQuery();
			
			if (resultSet.next()) { // 매개변수로 받은 휴대폰번호가 테이블에서 조회가 되는 경우 해당 휴대폰 번호의 아이디 개수를 저장
				cntPhone = resultSet.getInt(1);
			} 
			if (cntPhone > 2) { // 휴대폰 번호로 가입된 계정이 3개가 넘을 경우 false 즉, 더이상 회원가입 불가
				return false;
			} 
			
		} catch (Exception e) {System.out.println("회선가입수 쿼리문 오류");}
		finally {
			try {
				if (resultSet != null) {resultSet.close();}
				if (preparedStatement != null) {preparedStatement.close();}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {throw new RuntimeException(e.getMessage());}
		}
		return true; // 3개 이하일 경우 회원가입 가능이기 때문에 true
	}
	
	
	/* 로그인(메인 메소드에서 아이디 중복체크 진행 필요) */
	public int login(String userId, String userPassword) {
		String query = "select userNumber from tbl_user where userId = ? and userPassword = ?";
		
		try {
			connection = DBConnecter.getConnection(); // DB 연결
			preparedStatement = connection.prepareStatement(query); // 쿼리 실행
			preparedStatement.setString(1, userId); // 첫 번째 ?에 외부에서 입력받은 회원번호 삽입
			preparedStatement.setString(2, userPassword);
			resultSet = preparedStatement.executeQuery(); // 결과를 int형으로 반환받음
			
			if (resultSet.next()) {  // 만약 입력한 아이디와 비밀번호가 모두 일치하는 회원을 찾은 경우 해당 회원번호를 전역변수에 있는 loginStatus에 저장
				loginStatus = resultSet.getInt(1);
			}
		} catch (SQLException e) {
			System.out.println("로그인 쿼리문 오류");
			e.printStackTrace();
		}
		finally {
			try {
				if (resultSet != null) {resultSet.close();}
				if (preparedStatement != null) {preparedStatement.close();}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {throw new RuntimeException(e.getMessage());}
		}
		return loginStatus; // 로그인에 성공했다면 해당 회원의 회원번호를 리턴하지만, 그렇지 않은 경우 기존 저장되어 있던 0이 그대로 리턴되기 때문에 해당 부분은 메인 메소드에서 처리(비밀번호 틀림 등)
	}
	
	
	/* 아이디 찾기(여러 개 가입했을 수도 있음) */
	public ArrayList<String> findId(String name , String phoneNumber) {
		String query = "select userId from tbl_user where userName = ? and userPhoneNumber = ?";
		ArrayList<String> userIds = new ArrayList<String>(); // 가입한 회원의 이름과 휴대폰 번호가 일치하는 경우 해당 회원의 아이디 목록을 저장하기 위한 배열

		try {
			connection = DBConnecter.getConnection();
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, name);
			preparedStatement.setString(2, phoneNumber);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {userIds.add(resultSet.getString(1));} // 회원이 아이디를 몇 개 가입했는지 정확히 모르기 때문에 while을 통해 아이디 목록을 배열에 계속 추가

		} catch (SQLException e) {
			System.out.println("아이디 찾기 쿼리문 오류");
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null) {resultSet.close();}
				if (preparedStatement != null) {preparedStatement.close();}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {throw new RuntimeException(e.getMessage());}
		}
		return userIds;
	}


	/* 비밀번호 찾기(찾기와 변경 메소드는 따로 있음) */
	public boolean findPassword(String userId, String userPhoneNumber) {
		// 매개변수로 회원의 아이디와 핸드폰 번호를 입력받아 테이블에서 조건에 맞는 결과가 있을 때 회원번호 조회
		String query = "select userNumber from tbl_user where userId = ? and userPhoneNumber = ?";

		try {
			connection = DBConnecter.getConnection();
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, userId);
			preparedStatement.setString(2, userPhoneNumber);
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) { // 결과가 있는 경우
				passwordCheck = resultSet.getInt(1); // 전역변수에 있는 passwordCheck에 해당 회원번호를 저장
				return true; // 비밀번호 변경에서 사용하기 위해 검색결과가 있는 경우 true 사용
			}

		} catch (SQLException e) {
			System.out.println("비밀번호 찾기 쿼리문 오류");
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null) {resultSet.close();}
				if (preparedStatement != null) {preparedStatement.close();}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {throw new RuntimeException(e.getMessage());}
		}
		return false; // 입력받은 아이디와 핸드폰 번호에 일치하는 데이터를 못찾았을 때는 false를 반환한다
	}

	/* 비밀번호 변경(비밀번호 찾기 메소드 참조) */
	public void changePassword(String changePassword) {
		String query = "update tbl_user set userPassword = ? where userNumber = ?";
		
		try {
			connection = DBConnecter.getConnection();
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, changePassword);  // 매개변수로 받은 변경할 회원번호
			preparedStatement.setInt(2, passwordCheck); // 비밀번호 찾기에서 사용한 전역변수(회원번호)를 사용
			preparedStatement.executeUpdate();
			
		}catch (SQLException e) {
			System.out.println("비밀번호 변경 쿼리문 오류");
			e.printStackTrace();
		} finally {
			try {
				if (preparedStatement != null) {preparedStatement.close();}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {throw new RuntimeException(e.getMessage());}
		}
	}
	
	
//	--------------------------------------------------------------여기까지가 1번
	/* 회원정보 수정(해당 기능은 로그인 이후에 사용 가능한 기능 */
	public void updateUser(UserVO user) {
		// 회원번호, 포인트를 제외한 값을 매개변수로 받은 user로 수정
		String query = "update tbl_user set userPassword = ?, userName = ?, userPhoneNumber = ?, userLikeTeam = ?, userLikePlayer = ? where userNumber = ?";
		
		try {
			connection = DBConnecter.getConnection();
			preparedStatement = connection.prepareStatement(query); // sql 실행
			preparedStatement.setString(1, user.getUserPassword()); // userPw
			preparedStatement.setString(2, user.getUserName()); // userName
			preparedStatement.setString(3, user.getUserPhoneNumber()); // userPhone
			preparedStatement.setInt(4, user.getUserLikeTeam()); // userLikeTeam (int)
			preparedStatement.setInt(5, user.getUserLikePlayer()); // userLikePlayer (int)
			preparedStatement.setInt(6, loginStatus); // 현재 로그인한 회원(loginStatus)의 정보를 모두 수정
			preparedStatement.executeUpdate(); // 입력된 정보를 수정
			
		} catch (Exception e) {
			System.out.println("회원정보수정 쿼리 오류");
			e.printStackTrace();
		}
		finally {
			try {
				if (preparedStatement != null) {preparedStatement.close();}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {throw new RuntimeException(e.getMessage());}
		}
	}
	

	/* 내 정보 조회 */
	// 회원의 정보를 모두 출력해야 하기 때문에 UserVO 객체를 사용
	public UserVO selectUser() {
		String query = "select userNumber, userId, userName, userPhoneNumber, userLikeTeam, userLikePlayer, userPoint from tbl_user "
				+ "where userNumber=?"; // 해당 userNumber작성하면 회원 전체 정보를 보여준다.
		int i = 0; // 증감 연산을 사용하기 위함
		
		UserVO userVO = new UserVO(); // 테이블에 있는 회원의 모든 정보를 담기 위한 객체
		
		try {
			connection = DBConnecter.getConnection();
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, loginStatus); // 내 정보 조회 기능은 로그인 한 이후에 사용되는 기능이기 때문에 현재 로그인 한 회원번호를 저장하는 전역변수를 사용
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				userVO.setUserNumber(resultSet.getInt(++i)); // 회원번호~포인트까지 차례대로 값을 userVO 객체에 저장
				userVO.setUserId((resultSet.getString(++i)));
				userVO.setUserName(resultSet.getString(++i));
				userVO.setUserPhoneNumber(resultSet.getString(++i));
				userVO.setUserLikeTeam(resultSet.getInt(++i));
				userVO.setUserLikePlayer(resultSet.getInt(++i));
				userVO.setUserPoint(resultSet.getInt(++i));
			}
		} catch (SQLException e) {System.out.println("내 정보 조회 쿼리문 오류");}
		finally {
			try {
				if (resultSet != null) {resultSet.close();}
				if (preparedStatement != null) {preparedStatement.close();}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {throw new RuntimeException(e.getMessage());}
		}
		return userVO;
	}
	
	/* 회원탈퇴 */
	// 회원탈퇴의 경우 로그인을 진행한 후에 탈퇴가 진행되기 때문에 로그인이 되어 있는 상태에서만 가능하게끔 메인 메소드에서 처리 필요
	public boolean deleteUser(String password) {
		// 로그인한 회원의 회원번호를 통해 테이블에서 회원의 데이터 삭제
		String query = "delete from tbl_user where userNumber = ?";
		if(!selectUser().getUserPassword().equals(password)) {
			return false; // 삭제 완료
		}
		try {
			connection = DBConnecter.getConnection();
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, loginStatus); // 로그인 한 회원의 회원번호를 사용
			preparedStatement.executeUpdate();
			
		}catch (SQLException e) {
			System.out.println("회원탈퇴 쿼리문 오류");
			e.printStackTrace();
		} finally {
			try {
				if (preparedStatement != null) {preparedStatement.close();}
				if (connection != null) {connection.close();}
			} catch (SQLException e) { throw new RuntimeException(e.getMessage()); }
		}
		return true;
	}
	
	
	/* 전체 선수번호, 선수이름 */
	public ArrayList<String> printPlayerList() {
		String query = "select userLikePlayer, userLikePlayerName from tbl_favoriteplayer";
		ArrayList<String> list = new ArrayList<String>();
		try {
			connection = DBConnecter.getConnection();
			preparedStatement = connection.prepareStatement(query);
			resultSet = preparedStatement.executeQuery();
			
			while(resultSet.next()) {list.add(resultSet.getInt(1) + ". " + resultSet.getString(2));}
			
		}catch (SQLException e) {
			System.out.println("printPlayerList 쿼리문 오류");
			e.printStackTrace();
		} finally {
			try {
				if (preparedStatement != null) {preparedStatement.close();}
				if (connection != null) {connection.close();}
			} catch (SQLException e) { throw new RuntimeException(e.getMessage()); }
		}
		return list;
	}
	
	
	/* 전체 팀번호, 팀이름 */
	public ArrayList<String> printTeamList() {
		String query = "select userTeamNumber, userTeamName from tbl_soccerteam";
		ArrayList<String> list = new ArrayList<String>();
		try {
			connection = DBConnecter.getConnection();
			preparedStatement = connection.prepareStatement(query);
			resultSet = preparedStatement.executeQuery();
			
			while(resultSet.next()) {list.add(resultSet.getInt(1) + ". " + resultSet.getString(2));}
			
		}catch (SQLException e) {
			System.out.println("printTeamList 쿼리문 오류");
			e.printStackTrace();
		} finally {
			try {
				if (preparedStatement != null) {preparedStatement.close();}
				if (connection != null) {connection.close();}
			} catch (SQLException e) { throw new RuntimeException(e.getMessage()); }
		}
		return list;
	}
	
	
	/* 로그인 한 상태에서만 진행 */
	// 정답시 +10점
	public UserVO gameWin() {
		UserVO user = selectUser(); // 현재 로그인 한 회원의 정보를 userVO에 저장
		String query = "update tbl_user set userPoint = ? where userNumber = ?";
		
		try {
			connection = DBConnecter.getConnection();
			preparedStatement = connection.prepareStatement(query); // sql 실행
			preparedStatement.setInt(1, user.getUserPoint() + 10); // 현재 회원이 갖고 있는 포인트+10
			preparedStatement.setInt(2, loginStatus); // 현재 로그인한 회원(loginStatus)의 정보를 모두 수정
			preparedStatement.executeUpdate(); // 입력된 정보를 수정
			
		} catch (Exception e) {System.out.println("게임 승리 쿼리 오류");}
		finally {
			try {
				if (preparedStatement != null) {preparedStatement.close();}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {throw new RuntimeException(e.getMessage());}
		}
		return user;
	}
	
	// 오답시 -5점
	public UserVO gameLose() {
		UserVO user = selectUser(); // 현재 로그인 한 회원의 정보를 userVO에 저장
		String query = "update tbl_user set userPoint = ? where userNumber = ?";
		
		try {
			connection = DBConnecter.getConnection();
			preparedStatement = connection.prepareStatement(query); // sql 실행
			preparedStatement.setInt(1, user.getUserPoint() - 5); // 현재 회원이 갖고 있는 포인트-5
			preparedStatement.setInt(2, loginStatus); // 현재 로그인한 회원(loginStatus)의 정보를 모두 수정
			preparedStatement.executeUpdate(); // 입력된 정보를 수정
			
		} catch (Exception e) {System.out.println("게임 패배 쿼리 오류");}
		finally {
			try {
				if (preparedStatement != null) {preparedStatement.close();}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {throw new RuntimeException(e.getMessage());}
		}
		return user;
	}
	
//	--------------------------------------------------------------여기까지가 2번
	/* 로그아웃 */
	public void logOut() {
		if(loginStatus > 0) {
			loginStatus = 0;
		}
	}
	
	/* 로그인한 회원이 좋아하는 팀 번호를 return */
    /* 이후 팀이름 출력하는 메소드에서 사용*/
    public int findTeamNumber() {
        String query = "select userLikeTeam from tbl_user where userNumber = ?";
        
        try {
            connection = DBConnecter.getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, loginStatus); // loginStatus는 현재 로그인된 회원번호를 저장하고 있다
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {return resultSet.getInt(1);} // 로그인한 회원이 좋아하는 팀번호를 찾은 경우, 해당 팀 번호를 return
        } catch (SQLException e) {
            System.out.println("팀 번호 조회 쿼리문 오류");
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {resultSet.close();}
                if (preparedStatement != null) {preparedStatement.close();}
                if (connection != null) {connection.close();}
            } catch (SQLException e) {throw new RuntimeException(e.getMessage());}
        }
        return -1; // 좋아하는 팀번호가 없는 경우 -1을 반환(이후 메인 메소드에서 처리 필요)
    }
    
    
    /* findTeamNumber에서 리턴받은 팀번호를 활용한 팀 이름 찾기 */
    /* 팀 번호로 팀명 받아오기(회원정보조회에서 사용) */
    public String findTeamName() {
        // 로그인 한 회원이 좋아하는 팀 번호를 받아, 프리미어리그 팀의 번호와 이름이 저장되어 있는 tbl_favoriteTeam 테이블에서 팀번호와 일치하는 팀의 이름 검색
        String query = "select ts.userTeamName from "
        		+ "tbl_user tu join tbl_soccerteam ts "
        		+ "on tu.userLikeTeam = ts.userTeamNumber "
        		+ "where tu.userNumber = ?";
        String teamName = null; // 결과로 조회된 팀 이름을 저장할 변수
        
        try {
            connection = DBConnecter.getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, loginStatus);
            resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) { // 쿼리문의 결과가 있는 경우 팀이름을 teamName 변수에 저장
				teamName = resultSet.getString(1);
			}
            
        }catch (SQLException e) {
            System.out.println("팀 이름 찾기 쿼리문 오류");
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {resultSet.close();}
                if (preparedStatement != null) {preparedStatement.close();}
                if (connection != null) {connection.close();}
            } catch (SQLException e) { throw new RuntimeException(e.getMessage()); }
        }
        return teamName; // 데이터가 존재하지 않는 경우는 null을 리턴
    }
    
    
    /* 로그인한 회원이 좋아하는 선수 번호를 return */
    /* 선수이름, 소속팀을 출력하는 메소드에서 사용 */
    public int findPlayerNumber() {
        String query = "select userLikePlayer from tbl_user where userNumber = ?";
        try {
            connection = DBConnecter.getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, loginStatus); // loginStatus는 현재 로그인된 회원번호를 저장하고 있다
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) { return resultSet.getInt(1); } //로그인한 회원이 좋아하는 팀번호를 찾은 경우, 해당 번호를 return
        } catch (SQLException e) {
            System.out.println("선수 번호 조회 쿼리문 오류");
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {resultSet.close();}
                if (preparedStatement != null) {preparedStatement.close();}
                if (connection != null) {connection.close();}
            } catch (SQLException e) {throw new RuntimeException(e.getMessage());}
        }
        return -1; // 좋아하는 선수 번호가 없는 경우 -1을 반환(메인 메소드에서 처리 필요)
    }
    
    
    /* 로그인 한 회원이 좋아하는 선수 번호를 받아, 최애 선수 리스트에 있는 tbl_favoritePlayer 테이블에서 선수의 번호와 일치하는 선수이름을 리턴 */
    // 오버라이딩 된 메소드
	public String findPlayerInfo(int playerNum) {
		String query = "select tf.userLikePlayerName from "
				+ "tbl_favoriteplayer tf join tbl_user tu "
				+ "on tf.userLikePlayer = tu.userLikePlayer "
				+ "where tu.userLikePlayer= ? ";
        String playerName = null; // 로그인한 회원이 선택한 최애선수 번호를 저장
        
        try {
            connection = DBConnecter.getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, playerNum);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
            	playerName = resultSet.getString(1);
            } // 선수의 이름과 소속팀을 배열로 저장
            
        }catch (SQLException e) {
            System.out.println("최애선수 정보 찾기 쿼리문 오류");
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {resultSet.close();}
                if (preparedStatement != null) {preparedStatement.close();}
                if (connection != null) {connection.close();}
            } catch (SQLException e) { throw new RuntimeException(e.getMessage()); }
        }
        return playerName;
    }
	
    
    /* 로그인한 회원이 좋아하는 선수번호 선수 정보 소속팀 이름 찾기 */
    public String findPlayerInfo() {
        // 로그인 한 회원이 좋아하는 선수 번호를 받아, 최애 선수 리스트에 있는 tbl_favoritePlayer 테이블에서 선수의 번호와 일치하는 선수이름과 소속팀을 조회
        String query = "SELECT E1.Tname FROM "
        		+ "(select tf.userTeamNumber Tnum, tf.userLikePlayerName Pname, tf.userLikePlayer Pnum, ts.userTeamName Tname from "
        		+ "tbl_favoriteplayer tf join tbl_soccerteam ts "
        		+ "on tf.userTeamNumber = ts.userTeamNumber "
        		+ ") E1 JOIN tbl_user tu "
        		+ "ON E1.Pnum = tu.userLikePlayer "
        		+ "where tu.userNumber = ?";
        String teamName = null; // 로그인한 회원이 선택한 최애선수 번호를 저장
        
        try {
            connection = DBConnecter.getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, loginStatus);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
            	teamName = resultSet.getString(1);
            } // 선수의 이름과 소속팀을 배열로 저장
            
        }catch (SQLException e) {
            System.out.println("최애선수 정보 찾기 쿼리문 오류");
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {resultSet.close();}
                if (preparedStatement != null) {preparedStatement.close();}
                if (connection != null) {connection.close();}
            } catch (SQLException e) { throw new RuntimeException(e.getMessage()); }
        }
        return teamName;
    }
}