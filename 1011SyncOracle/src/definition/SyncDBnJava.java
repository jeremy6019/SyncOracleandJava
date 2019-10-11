package definition;

public class SyncDBnJava {
/*
 
 ** 데이터 베이스 연동 
 1. 프로젝트에 연동하고자 하는 데이터베이스의 드라이버를 Build path에 추가 
 
 2. 데이터베이스 드라이버 클래스를 Load 
 Class.forName("데이터베이스 드라이버 클래스");
 => 이 작업은 맨처음 한 번만 수행하면 됩니다. 
 
 3. 데이터베이스 연결 객체 생성 - java.sql.Connection
 => 데이터베이스 URL: 연결할 데이터베이스 위치
 => 데이터베이스에 접속할 계정  
 => 계정에 따른 비밀번호 
 Connection ? = DriverManager.getConnection(URL, 계정, 비밀번호); 
 => 트랜잭션 사용방법을 설정 
 ?.setAutoCommit(boolean)
 =>true를 대입하면 AutoCommit이 되서 commit이나 rollback을 호출하지 않아도
 SQL을 성공적으로 실행하면 자동으로 commit됩니다.
 false를 대입하면 Manual Commit이 되서 작업을 수행하고 나면 직접 commit이나 
 rollback을 호출해서 작업을 완료해야 합니다. 
 => 데이터베이스는 자바가 생성한 자원이 아니라서 사용이 끝나면 close()를 호출해서 
 연결을 해제해 주어야 합니다.
 =>데이터베이스 위치나 계정 및 비밀번호는 한 번 실행되면 수정될 가능성이 거의
 없는 코드입니다.
 프로젝트를 할 때는 실습용 계정을 가지고 작업을 하다가 배포를 할 때 실제계정으로 
 변경할 가능성이 높습니다. 
 이런 경우에는 별도의 파일이나 데이터베이스에 작성해두고 불러서 사용하는 것이 
 유지보수에 좋습니다.  
 
 4. SQL을 실행하기 위한 Statement객체 생성 
 1)Statement : 완성된 SQL을 실행 
 => 보안이 문제가 되고 문잘흘 표시하기 위한 따옴표 등을  직접 작성해야해서 
 데이터를 입력받아서 SQL을 생성하기가 어렵습니다. 
 
 2) PreparedStatement : 데이터를 바인딩해서 SQL을 만들어서 실행 
 => SQL을 만들때는 ?를 이용해서 나중에 데이터를 바인딩할 수있도록 만든 후에 
 데이터를 대입하는 형태로 SQL을 생성 
 => 따옴표를 직접 입력하지 않고 Java가 자동으로 입력하도록 할 수 있습니다.
 => 대다수의 프로그래밍언어는 이방식을 사용  
 
 3)CallableStatement: 프로시저 실행을 위한 Statement
 
5. SQL을 실행 
=>select를 제외한 구문은 영향받은 행의 개수를 리턴 

 =>select구문은 select의 결과에 접근할 수 있는 Cursor를 리턴 
 
6. 사용한 객체들을 close() 

**PreparedStatement 
=>SQL을 만들때는 ?를 이용해서 작성을 하고 나중에 데이터를 바인딩해서 SQL을 
완성해서 실행할 수 있는 Statement 

1.객체 생성 
Connection객체.prepareStatement(SQL); 
=> emp테이블에서 sal의 값이 3000이상인 데이터를 조회 
PrepareStatement pstmt = con.prepareStatement(
    "select * from emp where sal >= ?");

2. 물음표에 데이터를 바인딩 
PreparedStatement객체.set자료형(물음표인덱스, 실제데이터); 
=> pstmt.setInt(1, 3000); 
=>문자열 같은 경우 '를 자동으로 삽입해 줍니다. 

3.SQL을 실행 
 int executeUpdate(): select를 제외한  SQL을 싱행하고 영향받은 행의 개수를 리턴 
 
 ResultSet executeQuery(): select구문을 수행하고 결과를 리턴 
 => ResultSet rs = pstmt.executeQuery();
 
4.닫기는 close()호출 
 
** ResultSet
=>select 구문의 결과를 사용하기 위한 클래스 

1.boolean next() 
=> 다음에 사용할 데이터가 있으면 true를 리턴하고 없으면 false를 리턴 
=> true를 리턴한 경우에는 다음행 데이터에 접근할 수 있도록 다음행 데이터로 
포인터를 이동시킵니다.

2. 행에서 컬럼 1개를 읽는 메소드 
자료형 get자료형(컬럼의 인덱스 또는 컬럼이름); 
=>인덱스 번째나 컬럼이름에 해당하는 컬럼의 값을 자료형으로 리턴 
=>자료형을 잘못 지정하면 예외가 발생 
=>모든 컬럼은 String으로 받을 수 있습니다. 

3.닫기는 close()호출 
ResultSet rs = pstmt.executeQuery();
while(rs.next()){
   int empno = rs.getInt(1);
   String ename = rs.getString(2 또는 "ename");
   Date hiredate = rs.getDate("hiredate");
}

 **emp테이블의 모든 데이터를 가져와서 출력하기 
 emp테이블 
 - empno: 숫자 (정수 -22자리)
 - ename : 문자열(10자리-한글 3글자)
 - job: 문자열(9자리 - 한글 3자)
 - mgr: 숫자(정수 -22자리) 
 - hiredate:날짜 
 - sal: 숫자(정수 -22자리)
 - comm:숫자(정수-22자리, null이포함)
 - deptno: 숫자(정수 -22자리, DEPT테이블의 DEPTNO를 참조하는 외래키) 
 =>Null이 있는 경우는 다른 값으로 대체할 것인지 아니면 삭제할 것인지 판단 
 =>외래키로 설정된 컬럼의 값을 입력받고자 할 때는 직접 입력하도록 하는 것은 
 무결성 위반을 일으킬 가능성이 높기 때문에 이미 소유하고 있거나 콤보박스 같은 것으로
 입력을 제한할 필요가 있습니다. 
 => 기본키나 unique는 값을 삽입하기 전에 중복검사를 해주는 것이 좋습니다. 

 1. Java application프로젝 실행 
 
 2.ojdbc6.jar파일을 프로젝트에 추가하고 build Path에 추가 
 
 3. main메소드를 작성  
 
 	//
		try {
		//드라이버 클래스 로드 	
		    Class.forName(
				"oracle.jdbc.driver.OracleDriver" );
		//데이터 베이스 접속 
		    Connection con = 
				 DriverManager.getConnection(
						"jdbc:oracle:thin:"
						+ "@192.168.0.13:1521:xe",
						"user05", "user05" );
		    System.out.printf("접속 성공");
		    //SQL실행객체 만들기 
		    PreparedStatement pstmt = 
		    		con.prepareStatement(
		    				"select * from emp");
		   //SQL실행 
		    ResultSet rs = pstmt.executeQuery(); 
		    //select 구문의 결과 읽기 
		    while(rs.next()) {
		    	int empno = rs.getInt("empno");
		    	String ename = rs.getString("ename"); 
		    	String job = rs.getString("job"); 
		    	String mgr = rs.getString("mgr"); 
		    	Date hiredate = rs.getDate("hiredate"); 
		    	//null인 데이터를 자바에서 처리하고자 할 때는 
		    	//문자열로 받아서 null인 경우를 비교해서 작업 
		    	//오라클에서 처리할 때는 nvl(컬럼이름, 대체값) 
		    	String comm = rs.getString("comm");
		    	System.out.printf("사원번호:%d     사원이름:%s       직무:%s      "
		    			+ "관리자:%s       입사일:%s         상여금:%s\n ",
		    			empno, ename, job, mgr, hiredate, comm);
		    }
		   
		    rs.close();
		    pstmt.close();		    
		    con.close();
		
	    } catch(Exception e) {
	    	System.out.printf("%s\n", e.getMessage());
	    	e.printStackTrace();
	    }
		
** DTO, DAO 패턴 
1.DTO(Data Taransfer Object) 
=> 여러개의 속성을 묶어서 하나로 표현하기 위한 클래스 		
=> 가장 기본적인 형태는 각 속성을 private변수로 선언 
=> 각속성에 getter 와 setter생성 
=> 디버깅을 위해서 toString을 재정의 합니다. 

 => 그이외 추가적으로 Serializable(객체 직렬화- 파일에 기록할 수 있는 클래스로
 만들어주는 인터페이스), Comparable(크기 비교를 위한 인터페이스), Cloneable
 (객체 복제를 위한 인터페이스) 등을 구현합니다. 
 
 => 속성이름을 만들때는 대부분의 경우 테이블의 컬럼이름과 동일하게 만듭니다. 
 
 2.DAO(data Access Object) - Repository
 =>데이터를 읽고 쓰기 위한 클래스 
 => 데이터를 읽고 쓰는 부분을 별도의 클래스로 만들어서 사용 
 => 데이터를 읽고 쓰는 부분은 일반적인 알고리즘이나 도메인 지식이 필요없는 부분
 이라서 별도로 작성해야 가독성이 높아지고 유지보수도 편리 
 => 알고리즘이나 도메인 지식이 필요한 부분은 Service로 분리해서 구현 

=>메소드의 모양 
1)여러개의 데이터 검색 
List<하나의 자료형>으로 리턴 

2)기본키를 가지고 검색 
하나의 자료형으로 리턴 

3)그 이외 메소드 
영향받은 행의 개수를 리턴하던가 성공과 실패 여부를 리턴 

실습 - 이전에 만들어지 프로그램을 DTO와  DAO를 이용하는 형태로 수정 
1.EMP테이블의 데이터를 표현하기 위한  DTO클래스 생성 
=> domain 패키지의 EMP클래스로 생성 
public class EMP {

	private int empno; 
	private String ename; 
	private String job; 
	private int mgr;
	private Date hiredate; 
	private int sal; 
	private int comm; 
	private int deptno;
	
	public int getEmpno() {
		return empno;
	}
	public void setEmpno(int empno) {
		this.empno = empno;
	}
	public String getEname() {
		return ename;
	}
	public void setEname(String ename) {
		this.ename = ename;
	}
	public String getJob() {
		return job;
	}
	public void setJob(String job) {
		this.job = job;
	}
	public int getMgr() {
		return mgr;
	}
	public void setMgr(int mgr) {
		this.mgr = mgr;
	}
	public Date getHiredate() {
		return hiredate;
	}
	public void setHiredate(Date hiredate) {
		this.hiredate = hiredate;
	}
	public int getSal() {
		return sal;
	}
	public void setSal(int sal) {
		this.sal = sal;
	}
	public int getComm() {
		return comm;
	}
	public void setComm(int comm) {
		this.comm = comm;
	}
	public int getDeptno() {
		return deptno;
	}
	public void setDeptno(int deptno) {
		this.deptno = deptno;
	}
	
	@Override
	public String toString() {
		return "EMP [empno=" + empno + ", ename=" + ename + ", job=" + job + ", mgr=" + mgr + ", hiredate=" + hiredate
				+ ", sal=" + sal + ", comm=" + comm + ", deptno=" + deptno + "]";
	}
		
}

=> 이 클래스를 잘 만들어야 하는데 MyBatis나 Hibernate같은 프레임워크에서는 
이클래스를 데이터베이스 테이블과 매핑을 시켜서 직접 변수에 값을 넣는 코드를 
만들지 않아도 자동으로 수행합니다.
자료형이 정확하게 일치해야만 예외없이 수행

2.데이터베이스 작업을 위한 DAO클래스를 생성하고 이전에 작성한 코드를 
DAO클래스에 작성 
=> DAO패키지에 EMPDAO클래스를 생성  

**클래스가 로드될때 단 한번만 수행되는 코드 
static{
    클래스가 로드될 때 1번만 수행할 코드 
}
=> 이 블럭 안에는 인스턴스 변수를 사용하는 코드가 삽입되면 안됩니다. 

** 객체가 생성될 대 단 한번만 수행되는 코드 
{
  객체가 생성될 대 1번만 수행할 코드 
}
또는 
생성자 이용 
   
** Java에서 데이터베이스 연동을 하게되면 Connection, PreparedStatement, 
ResultSet이 사용됩니다,
거의 모든 메소드에서 사용이 되기 때문에 이러한 변수들은 인그턴스 변수로 만드는
것이 효율적 

데이터베이스 연결은 서버의 경우는 미리 연결해두고 close하지 않은 상태로 계속사용하도록
만드는 경우가 일반적이고 클라이언트의 경우는 작업을 할 때 연결하고
작업이 종료되면 연결을 해제합니다. 

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
	
}

 3. Main 클래스의 main메소드 수정 
 	//static 코드는 클래스 이름이 처음 사용될 때 호출 
		EMPDAO dao = new EMPDAO();
		System.out.printf("%s\n",
				dao.getList());
** 조회를 할 때 살펴봐야 할 내용은 몇 개의 행을 리턴하느냐 입니다. 
테이블의 전체 데이터를 조회 : 여러 개의 행 
테이블에서 기본키나 unique한 속성을 가지고 조회: 하나 또는 0개의 행 

여러 개의 행을 조회하는 경우에는 리턴타입이 List 
하나 또는 0개의 행을 조회하는 경우에는 리턴타입이 List가 아님 

List를 리턴할 때는 List객체를 생성해 두고 작업을 시작 
데이터가 없을 때는 List.size()가 0인지 확인 

하나를 조회하는 경우에는 리턴할 데이터에 null을 대입해두고 데이터를 찾으면 
그 때 객체를 생성해서 대입 
하나를 조회할 때는 조회된 데이터가 없으면 null을 리턴 
 
 
** empno(기본키0를 가지고 데이터를 조회 
=>기본키를 가지고 조회하는 메소드는 기본키 중복 검사나 상세보기를 할 때 사용 
회원가입을 할 때 입력한 아이디가 존재하는지 중복검사하는 경우나 게시판이나 블
로그에서 목록이 보여지는 상태에서 목록 중의 하나를 클릭하는 경우가 이 메소드를
호출하는 경우 

1.EMPDAO클래스에 메소드 구현 
=> null이 리턴되면 조건에 맞는 데이터가 없는 경우입니다.

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
		
2. main 메소드 수정해서 출력 


		// 7788인 데이터가 존재해서 출력 
		System.out.printf("%s\n",
				dao.getEMP(7788));
		// null이 리턴되서 출력되지 않음 
		System.out.printf("%s\n",
				dao.getEMP(7799));
 
 
 ** 데이터 삽입 
 => 데이터를 삽입할 때는 기본키 값을 어떻게 생성하는 지 확인 
 =>일련번호처럼 자동으로 삽입하는 경우는 바로 데이터를 삽입을 하면 되지만 
 아이디처럼 입력받아서 기본키를 생성하는 경우는 중복검사를 수행하고 중복
 검사를 통과하면 데이터를 삽입하도록 만들어주어야 합니다. 
=>select구문이 아닌 sql을 실행하는 메소드는 리턴값이 int입니다. 
=> 삽입과 수정은 모양이 같습니다. 

1.EMPDAO클래스에 데이터를 삽입하는 메소드를 생성 
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
				
				
			}catch(Exception e) {
				System.out.printf("데이터삽입 예외:%s\n",
						e.getMessage());
				e.printStackTrace();
			}
			return result;
			}
 
 => 삽입을 만들 때 주의할 점은 실패는 서버 오류가 아닌 이상 발생하지 않아야
 합니다. 
 =>삽입을 할 때는 삽입할 데이터의 유효성을 검사해서 유효성검사를 통과한 경우만
 삽입을 해주어야 합니다.
 기본키를 입력받는 경우 기본키 중복검사를 하고 중복검사를 통과한 경우만 삽입을 
 하도록 해주어야 합니다. 
 
 
** 데이터 삭제 
=> 삽입이나 수정을 할 때는 테이블의 모든 컬럼의 데이터가 필요하지만 삭제를 할 때는 
기본키만 가지고 합니다. 
=> 한 번 삭제되면 복원이 어렵기 때문에 삭제를 할 때는 정말로 삭제할 것인지 확인하는 
것이 좋습니다. 

1.EMPDAO클래스에 데이터를 삭제하는 메소드를 작성 

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

2.main 메소드 수정해서 실행 
	EMPDAO dao = new EMPDAO();
	    int r = JOptionPane.showConfirmDialog(
	    		null, "정말로 삭제", "데이터 삭제?",
	    		JOptionPane.YES_NO_OPTION);
	    if( r == JOptionPane.YES_OPTION) {
	    	    dao.deleteEMP(9999);
	    	    JOptionPane.showMessageDialog(null, "삭제 성공");
	    } else {
	    	JOptionPane.showMessageDialog(
	    			null, "삭제하지 않음");
	    }

** 파일을 저장 
=>숫자나 문자 그리고 날짜는 그대로 데이터베이스에 저장 
1. 파일 저장 
1) 파일을 만들어두고 파일의 경로를 저장하는 방법 
2) 파일의 내용을 저장
=> 대부분의 경우는 파일의 경로를 저장하지만 파일의 경로를 저장하면 
파일의 위치가 변경이 될때 뎅;터베이스에서도 같이 수정을 해야합니다.
=> 파일의 내용(바이트 배열)을 blob라는 자료형으로 데이터베이스에 
저장할 수 있습니다.  

2.blob와의 바인딩 
PreparesStatement.setBinaryStream(인덱스, FileInputStream객체, 길이)를 이용
해서 바인딩해서 저장할 수 있습니다. 

읽을 때는 ResultSet.getBinaryStream(인덱스 또는 컬럼이름)을 이용해서 읽으면 
내용이 InputStream객체로 리턴됩니다. 
이 내용을 가지고 FileOutputStream 객체에 삽입해서 원래 파일로 복원합니다. 

=> 최근에 이미지 데이터를 저장할 때 이 방식을 많이 취합니다. 

**BLOB 저장하고 읽기 
1.BLOB데이터를 저장할 수 있는 테이블을 1개 생성 
create table blobsample(
    filename varchar2(200),
    filecontent blob);
 
2.main메소드를 소유한 클래스를 만들고 저장하고 읽기 
	//1.blob저장하기 
		try {
			Connection con = DriverManager.getConnection(
			     	"jdbc:oracle:thin:"
			     	+ "@192.168.0.13:1521:xe",
				    "user05", "user05" );

			PreparedStatement pstmt = 
					con.prepareStatement(
							"insert into blobsample("
							+ "filename, filecontent) "
							+ "values(?,?)");
			
			String filepath = 
					"/Users/a503-22/Desktop/idol1.png";
			FileInputStream fis = 
					new FileInputStream(filepath);
			
			//파일경로에서 이름만 가져오기 
			// /로 분할 한 후 가장 마지막 문자열 
			String[] ar = filepath.split("/");
			String filename = ar[ar.length-1];
			
			System.out.println(filename);
			pstmt.setString(1, filename);
			//blob바인딩 
			pstmt.setBinaryStream(2,  fis);
			//실행 
			pstmt.executeUpdate();
			
			pstmt.close();
			con.close();
			
		} catch(Exception e) {
			System.out.printf("blob저장 예외:%s\n",
					e.getMessage());
			e.printStackTrace();
		}
*/		
/*		
			try {
				Connection con = DriverManager.getConnection(
				     	"jdbc:oracle:thin:"
				     	+ "@192.168.0.13:1521:xe",
					    "user05", "user05" );
		     
				PreparedStatement pstmt = 
						con.prepareStatement(
								"select * from blobsample");
				ResultSet rs = pstmt.executeQuery();
				while(rs.next()) {
					String filename = 
							rs.getString("filename");
					//blob 가져오기 
					InputStream is = 
							rs.getBinaryStream("filecontent");
					//현재 디렉토리에 filename을 갖는 
					// 파일 기록 객체를 생성 
					FileOutputStream fos =
							new FileOutputStream("./" +  filename);
					// is의 내용을 fos에 기록 
					while(true) {
						//한번에 읽을 배열을 생성 
						byte[] b = new byte[1024];
						// is의 내용을 읽어서 b에 저장
						//읽은 개수를 len에 저장  
						int len = is.read(b);
						//읽은 데이터가 없으면 그만 읽기 
						if(len <= 0) {
							break;
						}
						//읽은 내용을 fos에 기록 
						fos.write(b, 0, len);
					}
					// 기록을 하게되면 버퍼에 기록합니다. 
					//flush와 close를 호출해야만 기록이 완료 
				    fos.flush();
				    fos.close();
				}
				rs.close();
				pstmt.close();
				con.close();
				
			} catch(Exception e) {
				System.out.printf("blob읽기 예외:%s\n",
						e.getMessage());
				e.printStackTrace();
			}
	
**Procedure실행 
=>프로시저: 자주 사용하는 SQL구문을 하나의 이름으로 만들어 두고 사용하는 데이터 
베이스 개체 
사용하는 이유는 속도와 보안 때문 

1.Java에서 프로시저 실행 
CallableStatement 변수명 = Connection객체.prepareCall("{call프로시저이름
(?)}")

물음표가 있는 경우 실제 데이터로 바인딩 

프로시저가 select가 아니면 변수명.executeUpdate() 
select구문이면 변수명.executeQuery();

실습: dept테이블에 데이터를 삽입하는 프로시저를 만들고 자바에서 실행 
1.데이터베이스에서 프로시저를 생성 
CREATE OR REPLACE PROCEDURE myproc
(vdeptno dept.deptno%TYPE,
 vdname dept.dname%TYPE,
 vloc dept.loc%TYPE)
 IS
 BEGIN
	 INSERT INTO dept(deptno, dname, loc)
	 values(vdeptno, vdname, vloc);
 END;

2. main메소드를 수정해서 프로시저를 실행 
try {
				Connection con = DriverManager.getConnection(
				     	"jdbc:oracle:thin:"
				     	+ "@192.168.0.13:1521:xe",
					    "user05", "user05" );
			    //프로시저를 실행할 수 있는 객체를 생성 	
				CallableStatement call = 
						con.prepareCall(
								"{call myproc(?,?,?)}" );
				call.setInt(1, 77);
				call.setString(2,  "회계");
				call.setString(3, "영월");
				
				call.executeUpdate();
				
				call.close();
				con.close();
				
		    } catch(Exception e) {
		       System.out.printf("프로시저 실행 예외:%s\n",
			         e.getMessage());
		       e.printStackTrace();
	       }
 
 
 
 
 
 
 
 
 
  
 
 */
}
