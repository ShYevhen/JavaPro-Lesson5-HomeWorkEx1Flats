package net.ukr.shyevhen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.sql.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MyTest {
	private DbManager dbm = new DbManager();
	private Connection conn;

	@Before
	public void start() {
		dbm.connectToDB();
		dbm.clearDB();
		conn = dbm.getConn();
	}

	@After
	public void finish() {
		dbm.connClose();
	}

	@Test
	public void countTest() throws SQLException {
		countAddFlat();
		try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM flat");
				PreparedStatement psSuv = conn
						.prepareStatement("SELECT COUNT(*) FROM flat WHERE district='Suvorovsky'");
				PreparedStatement psMal = conn
						.prepareStatement("SELECT COUNT(*) FROM flat WHERE district='Malinovsky'");
				PreparedStatement psPri = conn.prepareStatement("SELECT COUNT(*) FROM flat WHERE district='Primorsky'");
				PreparedStatement psKie = conn.prepareStatement("SELECT COUNT(*) FROM flat WHERE district='Kievsky'")) {
			try (ResultSet rs = ps.executeQuery();
					ResultSet rsSuv = psSuv.executeQuery();
					ResultSet rsMal = psMal.executeQuery();
					ResultSet rsPri = psPri.executeQuery();
					ResultSet rsKie = psKie.executeQuery();) {
				assertTrue(rs.next());
				assertEquals(rs.getInt(1), 100);
				assertTrue(rsSuv.next());
				assertEquals(rsSuv.getInt(1), 25);
				assertTrue(rsMal.next());
				assertEquals(rsMal.getInt(1), 25);
				assertTrue(rsPri.next());
				assertEquals(rsPri.getInt(1), 25);
				assertTrue(rsKie.next());
				assertEquals(rsKie.getInt(1), 25);
			}
		}
	}

	private void countAddFlat() {
		for (int i = 0; i < 100; i++) {
			String district = "";
			if (i < 25) {
				district = "Suvorovsky";
			} else if (i < 50) {
				district = "Malinovsky";
			} else if (i < 75) {
				district = "Primorsky";
			} else {
				district = "Kievsky";
			}
			dbm.addFlat(district, "Strit" + (i < 10 ? "0" + i : i), 100 + i, i % 10,
					BigDecimal.valueOf((i + 1) * 10000));
		}
	}

	@Test
	public void checkParamTest() throws SQLException {
		try (Statement st = conn.createStatement()) {
			try (ResultSet rs = st.executeQuery("SELECT * FROM flat")) {
				for (int i = 0; rs.next(); i++) {
					if (i < 25) {
						assertEquals(rs.getString(2), "Suvorovsky");
					} else if (i < 50) {
						assertEquals(rs.getString(2), "Malinovsky");
					} else if (i < 75) {
						assertEquals(rs.getString(2), "Primorsky");
					} else {
						assertEquals(rs.getString(2), "Kievsky");
					}
					assertEquals(rs.getString(3), "Strit" + (i < 10 ? "0" + i : i));
					assertEquals(rs.getDouble(4), BigDecimal.valueOf(100 + i));
					assertEquals(rs.getInt(5), i % 10);
					assertEquals(rs.getBigDecimal(6), BigDecimal.valueOf((i + 1) * 10000));
				}
			}
		}
	}

	@Test
	public void changeDataTest() throws SQLException {
		dbm.clearDB();
		dbm.addFlat("Suvorovsky", "oDESSA", 100, 1, BigDecimal.valueOf(120));
		dbm.changeFlat("district", "Kievsky", 1);
		dbm.changeFlat("address", "Odessa Inglesi str.", 1);
		dbm.changeFlat(250.0, 1);
		dbm.changeFlat(12, 1);
		dbm.changeFlat(BigDecimal.valueOf(9999.99), 1);
		try (Statement st = conn.createStatement()) {
			try (ResultSet rs = st.executeQuery("SELECT * FROM flat WHERE id=1")) {
				rs.next();
				assertEquals(rs.getString(2), "Kievsky");
				assertEquals(rs.getString(3), "Odessa Inglesi str.");
				assertEquals(rs.getInt(4), 250);
				assertEquals(rs.getInt(5), 12);
				assertEquals(rs.getBigDecimal(6), BigDecimal.valueOf(9999.99));
			}
		}
	}
}
