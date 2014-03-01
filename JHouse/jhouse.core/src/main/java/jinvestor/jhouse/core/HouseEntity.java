package jinvestor.jhouse.core;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "house")
@Table(name = "homes")
public class HouseEntity {
	@Id
	private long zpid;

	@Column
	private String address;
	@Column
	private int squareFeet;
	@Column
	private int soldPrice;
	@Column
	private int latitude, longitude;
	@Column
	private Date lastSoldDate;
	@Column
	private int yearBuilt;
	@Column
	private float acres;

	// don't index on these, ever
	@Column
	private byte beds;
	@Column
	private float baths;

	public HouseEntity() {

	}

	public HouseEntity(HouseEntity other) {
		this.zpid = other.zpid;
		this.latitude = other.latitude;
		this.longitude = other.longitude;
		this.soldPrice = other.soldPrice;
		this.lastSoldDate = other.lastSoldDate;
		this.yearBuilt = other.yearBuilt;
		this.acres = other.acres;
		this.baths = other.baths;
		this.beds = other.beds;
		this.squareFeet = other.squareFeet;
		this.address = other.address;
	}

	public HouseEntity(House house) {
		this.zpid = house.getZpid();
		this.latitude = house.getLatitude();
		this.longitude = house.getLongitude();
		this.soldPrice = house.getSoldPrice();
		this.lastSoldDate = house.getLastSoldDate();
		this.yearBuilt = house.getYearBuilt();
		this.acres = house.getAcres();
		this.baths = house.getBaths();
		this.beds = house.getBeds();
		this.squareFeet = house.getSquareFeet();
		this.address = house.getAddress();
	}

	public long getZpid() {
		return zpid;
	}

	public void setZpid(long zpid) {
		this.zpid = zpid;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getSquareFeet() {
		return squareFeet;
	}

	public void setSquareFeet(int squareFeet) {
		this.squareFeet = squareFeet;
	}

	public int getSoldPrice() {
		return soldPrice;
	}

	public void setSoldPrice(int soldPrice) {
		this.soldPrice = soldPrice;
	}

	public int getLatitude() {
		return latitude;
	}

	public void setLatitude(int latitude) {
		this.latitude = latitude;
	}

	public int getLongitude() {
		return longitude;
	}

	public void setLongitude(int longitude) {
		this.longitude = longitude;
	}

	public Date getLastSoldDate() {
		return lastSoldDate;
	}

	public void setLastSoldDate(Date lastSoldDate) {
		this.lastSoldDate = lastSoldDate;
	}

	public int getYearBuilt() {
		return yearBuilt;
	}

	public void setYearBuilt(int yearBuilt) {
		this.yearBuilt = yearBuilt;
	}

	public float getAcres() {
		return acres;
	}

	public void setAcres(float acres) {
		this.acres = acres;
	}

	public byte getBeds() {
		return beds;
	}

	public void setBeds(byte beds) {
		this.beds = beds;
	}

	public float getBaths() {
		return baths;
	}

	public void setBaths(float baths) {
		this.baths = baths;
	}

}
