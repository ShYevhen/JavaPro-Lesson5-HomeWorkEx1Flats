package net.ukr.shyevhen;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
	private static Scanner sc = new Scanner(System.in);

	public static void main(String[] args) {
		DbManager dbm = new DbManager();
		dbm.connectToDB();
		for (;;) {
			System.out.println("1: get flat list \r\n2: add new flat \r\n3: change the flat "
					+ "\r\n4: delete the flat \r\n5: search by parameter \r\n6: delete all");
			try {
				String number = sc.nextLine();
				if (number.equals("1")) {
					System.out.println(dbm.getFlatList());
				} else if (number.equals("2")) {
					addFlat(dbm);
				} else if (number.equals("3")) {
					changeFlat(dbm);
				} else if (number.equals("4")) {
					delFlat(dbm);
				} else if (number.equals("5")) {
					getTheFlats(dbm);
				} else if (number.equals("6")) {
					dbm.clearDB();
				} else {
					sc.close();
					break;
				}
			} catch (IllegalStateException | NoSuchElementException e) {
				e.printStackTrace();
			}
		}
		dbm.connClose();
	}

	private static void addFlat(DbManager dbm) {
		String district = getDistrict();
		System.out.println("Address:");
		String address = sc.nextLine();
		System.out.println("Square:");
		double square = sc.nextDouble();
		sc.nextLine();
		System.out.println("Number of rooms:");
		int rooms = sc.nextInt();
		sc.nextLine();
		System.out.println("Price:");
		BigDecimal price = sc.nextBigDecimal();
		sc.nextLine();
		dbm.addFlat(district, address, square, rooms, price);
		System.out.println(dbm.getFlatList());
	}

	private static void changeFlat(DbManager dbm) {
		System.out.println("Input id flat whrere you want to change");
		int id = sc.nextInt();
		sc.nextLine();
		while (true) {
			System.out.println("Select what you want to change");
			System.out.println("1: district \r\n2: address \r\n3: square \r\n4: rooms \r\n5: price \r\nEnter: exit");
			String change = sc.nextLine();
			if (change.equals("1")) {
				dbm.changeFlat("district", getDistrict(), id);
			} else if (change.equals("2")) {
				System.out.println("New address:");
				dbm.changeFlat("address", sc.nextLine(), id);
			} else if (change.equals("3")) {
				System.out.println("New square:");
				dbm.changeFlat(sc.nextDouble(), id);
				sc.nextLine();
			} else if (change.equals("4")) {
				System.out.println("New number of rooms:");
				dbm.changeFlat(sc.nextInt(), id);
				sc.nextLine();
			} else if (change.equals("5")) {
				System.out.println("New price:");
				dbm.changeFlat(sc.nextBigDecimal(), id);
				sc.nextLine();
			} else {
				return;
			}
		}
	}

	private static void delFlat(DbManager dbm) {
		System.out.println("Input id flat than you want to delete");
		int id = Integer.parseInt(sc.nextLine());
		dbm.delFlat(id);
	}

	private static void getTheFlats(DbManager dbm) {
		System.out.println("Select parammetr to search");
		System.out.println("1: district \r\n2: address \r\n3: square \r\n4: rooms \r\n5: price \r\nEnter: exit");
		String change = sc.nextLine();
		if (change.equals("1")) {
			System.out.println(dbm.getTheFlats("district", getDistrict()));
		} else if (change.equals("2")) {
			System.out.println("Address:");
			System.out.println(dbm.getTheFlats("address", sc.nextLine()));
		} else if (change.equals("3")) {
			System.out.println("Square:");
			System.out.println(dbm.getTheFlats("square", getMin("square"), getMax("square")));
		} else if (change.equals("4")) {
			System.out.println("Number of rooms:");
			System.out.println(dbm.getTheFlats("rooms", getMin("rooms"), getMax("rooms")));
		} else if (change.equals("5")) {
			System.out.println("Price:");
			System.out.println(dbm.getTheFlats("price", getMin("price"), getMax("price")));
		} else {
			return;
		}
	}

	private static int getMin(String name) {
		System.out.println("Input min integer " + name + " or press Enter");
		String minSt = sc.nextLine();
		if (minSt == null || "".equals(minSt)) {
			return 0;
		} else {
			return Integer.parseInt(minSt);
		}
	}

	private static int getMax(String name) {
		System.out.println("Input max integer" + name + " or press Enter");
		String maxSt = sc.nextLine();
		if (maxSt == null || "".equals(maxSt)) {
			return Integer.MAX_VALUE;
		} else {
			return Integer.parseInt(maxSt);
		}
	}

	private static String getDistrict() {
		String district = null;
		do {
			System.out.println("District");
			System.out.println("1: Suvorovsky");
			System.out.println("2: Malinovsky");
			System.out.println("3: Primorsky");
			System.out.println("4: Kievsky");
			switch (sc.nextLine()) {
			case "1":
				district = "Suvorovsky";
				break;
			case "2":
				district = "Malinovsky";
				break;
			case "3":
				district = "Primorsky";
				break;
			case "4":
				district = "Kievsky";
				break;
			default:
				break;
			}
		} while (district == null);
		return district;
	}

}
