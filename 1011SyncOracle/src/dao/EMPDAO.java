package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import domain.EMP;

public class EMPDAO {
		
	static {
	
         try {
    	 // 오라클 드라이버클래스 로드
    	 // 이코드에서 예외가 발생하면 
    	 // jar파일이 build path에 추가되었는지 
    	 // 드라이버 클래스 이름이 맞는지 확인 
    	      Class.forName(
    				"oracle.jdbc.driver.OracleDriver" );
       //       System.out.printf("클래스가 로드됨");
          } catch(Exception e) {  	 
    	     System.out.printf("드라이버 클래스");    
         }
	} 
	 // 데이터베이스 사용을 위해서 자주 사용하는 변수를 
	// 선언 
	private Connection con; 
	private PreparedStatement pstmt; 
	private ResultSet rs;
	
	//emp테이블의 전체 데이터를 가져와서 리턴하는 메소드 
	//조회할 때 where절을 사용하면 매개변수를 만들고 
	//where절이 없을 때 매개변수도 없습니다. 
	public List<EMP> getList(){
		
		//List를 리턴할 때는 List객체를 생성하고 
		// 시작 
		//데이터를 읽지 못하면 리스트의 사이즈가 0 

		List<EMP> list =
				new ArrayList<EMP>();	
		try {
			    con = DriverManager.getConnection(
				     	"jdbc:oracle:thin:@192.168.0.13:1521:xe",
					    "user05", "user05" );
			    pstmt = con.prepareStatement(
					    "select * from emp");			  
			    rs = pstmt.executeQuery();
			    
			   while(rs.next()) {
				   //하나의 데이터를 저장할 객체를 생성 
				   EMP emp = new EMP();
				   //각 컬럼을 읽어서 DTO에 저장 
				   emp.setEmpno(rs.getInt("empno"));
				   emp.setEname(rs.getString("ename"));
				   emp.setJob(rs.getString("job"));
				   emp.setMgr(rs.getInt("mgr"));
				   emp.setHiredate(rs.getDate("hiredate"));
				   emp.setSal(rs.getInt("sal"));
				   emp.setComm(rs.getInt("comm"));
				   emp.setDeptno(rs.getInt("deptno"));			
				   //읽은 내용을 리스트에 추가 
				   list.add(emp);
				   
			   }
			   
		} catch(Exception e) {
			System.out.printf("데이터 읽기 예외:%s\n",
					e.getMessage());
			e.printStackTrace();
		}
		
		return list;
	}
	
	
	
		//기본키인 empno를 이용해서 데이터를 조회하는 메소드
	public EMP getEMP(int empno) {
		EMP emp =null;
		//데이터베이스 연결 
		try {
		    con = DriverManager.getConnection(
			     	"jdbc:oracle:thin:@192.168.0.13:1521:xe",
				    "user05", "user05" );
		    //SQL실행객체 생성 
		    pstmt = con.prepareStatement(
		    		"select * from emp where empno = ? ");
		    //?에 실제 데이터를 바인딩 
		    pstmt.setInt(1, empno);
		    //SQL실행 
		    rs = pstmt.executeQuery();
		    //데이터 읽기 
		    if( rs.next()) {
		    	emp = new EMP(); 
		    	emp.setEmpno(rs.getInt("empno"));
		    	emp.setEname(rs.getString("ename"));
				emp.setJob(rs.getString("job"));
				emp.setMgr(rs.getInt("mgr"));
			    emp.setHiredate(rs.getDate("hiredate"));
				emp.setSal(rs.getInt("sal"));
				emp.setComm(rs.getInt("comm"));
				emp.setDeptno(rs.getInt("deptno"));		
				
		    }
		    rs.close();
		    pstmt.close();
		    con.close();
		} catch(Exception e) {
			System.out.printf("1개 가져오기 예외:%s\n",
					e.getMessage());
			e.printStackTrace();
		}	
		return emp;
	  }	

	
		//DTO 1개를 매개변수로 받아서 데이터를 삽입하는 메소드 
		public int insertEMP(EMP emp) {
			int result = -1;
			try {
			//데이터베이스 연결 
				 con = DriverManager.getConnection(
					     	"jdbc:oracle:thin:@192.168.0.13:1521:xe",
						    "user05", "user05" );
			//SQL실행객체 생성 
				 pstmt = con.prepareStatement(
						 "insert into emp("
						 +"empno, ename, job, mgr,"
						 + "hiredate, sal, comm, deptno) "
						 + "values(?,?,?,?,?,?,?,?)");
				 pstmt.setInt(1,  emp.getEmpno());
				 pstmt.setString(2,  emp.getEname());
				 pstmt.setString(3,  emp.getJob());
				 pstmt.setInt(4,  emp.getMgr());
				 pstmt.setDate(5,  emp.getHiredate());
				 pstmt.setInt(6,  emp.getSal());
				 pstmt.setInt(7,  emp.getComm());
				 pstmt.setInt(8,  emp.getDeptno());
				 
		        //SQL실행 
				result = pstmt.executeUpdate();
		        //select인 경우 리턴값 작업 
				pstmt.close();
				con.close();
				
			}catch(Exception e) {
				System.out.printf("데이터삽입 예외:%s\n",
						e.getMessage());
				e.printStackTrace();
			}
			return result;
			
		}

		
		// empno를 매개변수로 받아서 데이터를 삭제하는 메소드 
		public int deleteEMP(int empno) {
			int result = -1;
			try {
				//데이터베이스 연결 
				 con = DriverManager.getConnection(
					     	"jdbc:oracle:thin:@192.168.0.13:1521:xe",
						    "user05", "user05" );
				//데이터를 삭제하는 SQL객체 생성 
				 pstmt = con.prepareStatement(
						 "delete from emp where empno = ? " );
				 pstmt.setInt(1,  empno);
				 //실행 
				 result = pstmt.executeUpdate();
				 //정리 
				 pstmt.close();
				 con.close();
				 
			} catch(Exception e) {
				System.out.printf(
						"데이터 삭제 예외:%s\n", e.getMessage());
				e.printStackTrace();
			}
					
			return result;
		}
		

		
	}
	

