package net.ukr.shyevhen;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Properties;
import java.sql.*;

public class DbManager {
	private Connection conn;

	public DbManager() {
		super();
	}

	public Connection getConn() {
		return conn;
	}

	public void connectToDB() {
		InputStream is = getClass().getClassLoader().getResourceAsStream("db.properties");
		Properties prop = new Properties();
		try {
			prop.load(is);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		String user = prop.getProperty("db.user");
		String password = prop.getProperty("db.password");
		String url = prop.getProperty("db.url");
		try {
			conn = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void clearDB() {
		try (Statement st = conn.createStatement()) {
			st.execute("DROP TABLE IF EXISTS flat");
			st.execute("CREATE TABLE flat (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
					+ "district ENUM('Suvorovsky','Malinovsky','Primorsky','Kievsky') DEFAULT NULL, "
					+ "address VARCHAR(128) NOT NULL, square DOUBLE(7,2) NOT NULL, "
					+ "rooms INT NOT NULL, price DEC(12,2) NOT NULL)");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void addFlat(String district, String address, double square, int rooms, BigDecimal price) {
		try (PreparedStatement pstAdd = conn.prepareStatement(
				"INSERT INTO flat (district, address, square, rooms, price) VALUES (?, ?, ?, ?, ?)")) {
			pstAdd.setString(1, district);
			pstAdd.setString(2, address);
			pstAdd.setDouble(3, square);
			pstAdd.setInt(4, rooms);
			pstAdd.setBigDecimal(5, price);
			pstAdd.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void delFlat(int id) {
		try (Statement st = conn.createStatement()) {
			st.executeUpdate("DELETE FROM flat WHERE id=" + id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void changeFlat(String name, String value, int id) {
		try {
			PreparedStatement ps = null;
			try {
				if (name.equals("district")) {
					ps = conn.prepareStatement("UPDATE flat SET district=? WHERE id=?");
				} else if (name.equals("address")) {
					ps = conn.prepareStatement("UPDATE flat SET address=? WHERE id=?");
				}
				ps.setString(1, value);
				ps.setInt(2, id);
				ps.execute();
			} finally {
				ps.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void changeFlat(Double value, int id) {
		try (PreparedStatement ps = conn.prepareStatement("UPDATE flat SET square=? WHERE id=?")) {
			ps.setDouble(1, value);
			ps.setInt(2, id);
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void changeFlat(int value, int id) {
		try (PreparedStatement ps = conn.prepareStatement("UPDATE flat SET rooms=? WHERE id=?")) {
			ps.setInt(1, value);
			ps.setInt(2, id);
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void changeFlat(BigDecimal value, int id) {
		try (PreparedStatement ps = conn.prepareStatement("UPDATE flat SET price=? WHERE id=?")) {
			ps.setBigDecimal(1, value);
			ps.setInt(2, id);
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getFlatList() {
		StringBuilder sb = new StringBuilder("FlatList").append(System.lineSeparator());
		try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM flat"); ResultSet rs = ps.executeQuery()) {
			tableCreate(rs, sb);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public String getTheFlats(String name, String value) {
		StringBuilder sb = new StringBuilder();
		try {
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				if (name.equals("district")) {
					ps = conn.prepareStatement("SELECT * FROM flat WHERE district=?");
					ps.setString(1, value);
				} else if (name.equals("address")) {
					ps = conn.prepareStatement("SELECT * FROM flat WHERE address LIKE ?");
					ps.setString(1, "%" + value + "%");
				}
				rs = ps.executeQuery();
				tableCreate(rs, sb);
			} finally {
				rs.close();
				ps.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public String getTheFlats(String name, int min, int max) {
		StringBuilder sb = new StringBuilder();
		try {
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				if (name.equals("square")) {
					ps = conn.prepareStatement("SELECT * FROM flat WHERE square>=? AND square<=?");
				} else if (name.equals("rooms")) {
					ps = conn.prepareStatement("SELECT * FROM flat WHERE rooms>=? AND rooms<=?");
				} else if (name.equals("price")) {
					ps = conn.prepareStatement("SELECT * FROM flat WHERE price>=? AND price<=?");
				}
				ps.setInt(1, min);
				ps.setInt(2, max);
				rs = ps.executeQuery();
				tableCreate(rs, sb);
			} finally {
				rs.close();
				ps.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	private void tableCreate(ResultSet rs, StringBuilder sb) throws SQLException {
		ResultSetMetaData md = rs.getMetaData();
		for (int i = 1; i <= md.getColumnCount(); i++) {
			sb.append(md.getColumnName(i) + "\t\t");
		}
		sb.append(System.lineSeparator());
		for (; rs.next();) {
			for (int i = 1; i <= md.getColumnCount(); i++) {
				if (i == 1 || i == 5) {
					sb.append(rs.getInt(i) + "\t\t");
				} else if (i == 2 || i == 3) {
					sb.append(rs.getString(i) + "\t\t");
				} else if (i == 4) {
					sb.append(rs.getDouble(i) + "\t\t");
				} else if (i == 6) {
					sb.append(rs.getBigDecimal(i) + "\t\t");
				}
			}
			sb.append(System.lineSeparator());
		}
	}

	public void connClose() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		conn.close();
	}

}
