package src.dbtesting;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import oracle.jdbc.OracleTypes;
import oracle.jdbc.pool.OracleDataSource;

public class OurTesting {

	/**
	 * Main function to start the menu driven program.
	 * 
	 * @param args arguments to pass to the program. args[0] is username and args[1]
	 *             is password.
	 * @throws SQLException
	 */
	public static void main(String[] args) throws SQLException {
		if (args.length != 2) {
			System.out.println("Invalid arguments. Enter 2 arguments as username and password.");
			return;
		}
		Connection conn = null;
		OracleDataSource ds = null;
		Scanner sc = new Scanner(System.in);
		try {
			ds = new oracle.jdbc.pool.OracleDataSource();
			// database source location
			ds.setURL("jdbc:oracle:thin:@castor.cc.binghamton.edu:1521:ACAD111");

			String username = args[0];
			String password = args[1];

			try {
				// database connection with given username and password.
				conn = ds.getConnection(username, password);
				System.out.println("\nSuccessfully connected to Oracle database");
			} catch (Exception e) {
				System.err.println("Connection Error: " + e.getMessage());
				System.exit(1);
			}
		}

		catch (SQLException ex) {
			System.out.println("\n*** SQLException caught ***\n" + ex.getMessage());
			System.exit(1);
		} catch (Exception e) {
			System.out.println("\n*** other Exception caught ***\n" + e.getMessage());
			if (conn != null) {
				conn.close();
			}
			System.exit(1);
		}

		while (true) {

			try {
				System.out.print("\n******* WELCOME TO STUDENT SERVICE SYSTEM *******\n");
				System.out.print("\nEnter '1' to show all the tables\n");
				System.out.print(
						"Enter '2' to List B#, First name and Last name of every student in class using classid\n");
				System.out.print("Enter '3' to check the prerequisite courses for a given course\n");
				System.out.print("Enter '4' to enroll graduate student into class\n");
				System.out.print("Enter '5' to drop graduate student from class\n");
				System.out.print("Enter '6' to delete student from students table\n");
				System.out.print("Enter '7' to exit\n");

				int option = sc.nextInt();
				CallableStatement call;

				if (option == 7) {
					System.out.println("Database disconnected! Thank You! Exiting now");
					conn.close();
					System.exit(0);
				}
				boolean innerFlag = true;
				switch (option) {
				case 1:
					while (innerFlag) {
						System.out.println("Select option for table ");
						System.out.println("Enter '1' for Students table");
						System.out.println("Enter '2' for Courses table");
						System.out.println("Enter '3' for Course Credit table");
						System.out.println("Enter '4' for Classes table");
						System.out.println("Enter '5' for G_enrollments table");
						System.out.println("Enter '6' for Score_Grade table");
						System.out.println("Enter '7' for Prerequisites table");
						System.out.println("Enter '8' for Logs table");
						System.out.println("Enter '9 to go back");
						System.out.println("Enter '0' to exit");

						int op = sc.nextInt();

						switch (op) {

						case 1:
							call = conn.prepareCall("begin myPkg.show_students(?); end;",
									ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
							call.registerOutParameter(1, OracleTypes.CURSOR);
							call.execute();
							printResult((ResultSet) call.getObject(1));
							call.close();

							break;

						case 2:
							call = conn.prepareCall("begin myPkg.show_courses(?);end;");
							call.registerOutParameter(1, OracleTypes.CURSOR);
							call.execute();
							printResult((ResultSet) call.getObject(1));
							call.close();
							break;

						case 3:
							call = conn.prepareCall("begin myPkg.show_course_credits(?);end;");
							call.registerOutParameter(1, OracleTypes.CURSOR);
							call.execute();
							printResult((ResultSet) call.getObject(1));
							call.close();
							break;

						case 4:
							call = conn.prepareCall("begin myPkg.show_classes(?);end;");
							call.registerOutParameter(1, OracleTypes.CURSOR);
							call.execute();
							printResult((ResultSet) call.getObject(1));
							call.close();
							break;

						case 5:
							call = conn.prepareCall("begin myPkg.show_enrollments(?);end;");
							call.registerOutParameter(1, OracleTypes.CURSOR);
							call.execute();
							printResult((ResultSet) call.getObject(1));
							call.close();
							break;

						case 6:
							call = conn.prepareCall("begin myPkg.show_grade(?);end;");
							call.registerOutParameter(1, OracleTypes.CURSOR);
							call.execute();
							printResult((ResultSet) call.getObject(1));
							call.close();
							break;

						case 7:
							call = conn.prepareCall("begin myPkg.show_preq(?);end;");
							call.registerOutParameter(1, OracleTypes.CURSOR);
							call.execute();
							printResult((ResultSet) call.getObject(1));
							call.close();
							break;

						case 8:
							call = conn.prepareCall("begin myPkg.show_logs(?);end;");
							call.registerOutParameter(1, OracleTypes.CURSOR);
							call.execute();
							printResult((ResultSet) call.getObject(1));
							call.close();
							break;

						case 9:
							innerFlag = false;
							break;

						case 0:
							System.out.println("Database disconnected! Thank You! Exiting now");
							conn.close();
							sc.close();
							System.exit(0);
							break;

						default:
							System.out.println("Invalid option");
							break;
						}
					}
					break;

				case 2:
					// list the students enrolled in a class.
					System.out.println("Enter class id");
					String classid = sc.next();

					call = conn.prepareCall("begin myPkg.display_std_by_classid(?,?); end;");
					call.registerOutParameter(1, OracleTypes.CURSOR);
					call.setString(2, classid);
					call.execute();
					printResult((ResultSet) call.getObject(1));
					call.close();
					break;

				case 3:
					// list the prerequisites using the dept_code and course number.
					System.out.println("Enter department code");
					String deptc = sc.next();

					System.out.println("Enter course number");
					String course_no = sc.next();

					call = conn.prepareCall("begin myPkg.display_preq_by_dept(?,?,?); end;");
					call.registerOutParameter(1, OracleTypes.CURSOR);
					call.setString(2, deptc.toUpperCase());
					call.setString(3, course_no);
					call.execute();
					printResult((ResultSet) call.getObject(1));
					call.close();
					break;

				case 4:
					// enroll student into a course using a bNumber and classid.
					System.out.println("Enter B#");
					String bnum = sc.next();

					System.out.println("Enter classid");
					String classid2 = sc.next();

					call = conn.prepareCall("begin myPkg.student_enrollment(?,?); end;");
					call.setString(1, bnum);
					call.setString(2, classid2);
					call.execute();
					call.close();
					System.out.println("Student '" + bnum + "' has been enrolled in class: " + classid2);
					break;

				case 5:
					// drop student course from g_enrollments table.
					System.out.println("Enter B#");
					String bnum2 = sc.next();
					System.out.println("Enter classid");
					String classid3 = sc.next();
					call = conn.prepareCall("begin myPkg.student_drop(?,?); end;");
					call.setString(1, bnum2);
					call.setString(2, classid3);
					call.execute();
					call.close();
					System.out.println("Student '" + bnum2 + "' has been dropped from class: " + classid3);
					break;

				case 6:
					// remove student from student table and entry from g_enrollments table.
					System.out.println("Enter B#");
					String bnum3 = sc.next();
					call = conn.prepareCall("begin myPkg.remove_student(?); end;");
					call.setString(1, bnum3);
					call.execute();
					call.close();
					System.out.println("Student '" + bnum3 + "' has been deleted successfully!");
					break;

				}

			}

			catch (SQLException ex) {
				System.out.println("\nERROR: " + ex.getMessage().split("\n")[0].split(":")[1]);

			} catch (Exception e) {
				System.out.println("Other Exception caught: " + e.getMessage());
				if (conn != null) {
					conn.close();
				}
			}
		}
	}

	/**
	 * prints the query data in tabular format.
	 * 
	 * @param resultSet contains the data of the query.
	 * @throws SQLException
	 */
	private static void printResult(ResultSet resultSet) throws SQLException {

		DBTablePrinter.printResultSet(resultSet);

		// legacy printing code. not well formatted.
		/*
		 * ResultSetMetaData rsmd = resultSet.getMetaData(); int columnsNumber =
		 * rsmd.getColumnCount();
		 * 
		 * for (int i = 1; i <= columnsNumber; i++) {
		 * System.out.print(rsmd.getColumnName(i)+ "\t"); } System.out.println("");
		 * 
		 * while (resultSet.next()) { for (int i = 1; i <= columnsNumber; i++) { String
		 * columnValue = resultSet.getString(i); System.out.print(columnValue + "\t\t");
		 * } System.out.println(""); }
		 */
	}

}
