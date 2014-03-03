package jinvestor.jhouse.core;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.hadoop.io.Writable;

@Entity(name = "house")
@Table(name = "homes")
public class HouseEntity implements Writable {
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
		if (other.lastSoldDate!=null) {
			this.lastSoldDate = new Date(other.lastSoldDate.getTime());
		} else {
			this.lastSoldDate=null;
		}
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
		if (house.getLastSoldDate()!=null) {
			this.lastSoldDate = new Date(house.getLastSoldDate().getTime());
		} else {
			this.lastSoldDate=null;
		}
		this.yearBuilt = house.getYearBuilt();
		this.acres = house.getAcres();
		this.baths = house.getBaths();
		this.beds = house.getBeds();
		this.squareFeet = house.getSquareFeet();
		this.address = house.getAddress();
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof HouseEntity)) {
			return false;
		}
		HouseEntity that = (HouseEntity) o;
		EqualsBuilder eb = new EqualsBuilder().append(zpid, that.zpid)
				.append(latitude, that.latitude)
				.append(longitude, that.longitude)
				.append(soldPrice, that.soldPrice)
				.append(lastSoldDate, that.lastSoldDate)
				.append(yearBuilt, that.yearBuilt).append(acres, that.acres)
				.append(baths, that.baths).append(beds, that.beds)
				.append(squareFeet, that.squareFeet)
				.append(address, that.address);
		return eb.isEquals();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder().append(zpid)
				.append(latitude).append(longitude).append(soldPrice)
				.append(lastSoldDate).append(yearBuilt).append(acres)
				.append(baths).append(beds).append(squareFeet).append(address);
		return hcb.toHashCode();
	}

	public String toString() {
		return String.format("%d: %s", zpid, address);
	}

	public long getZpid() {
		return zpid;
	}

	public void setZpid(long zpid) {
		this.zpid = zpid;
	}

	public void setZpid(int zpid) {
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
		this.lastSoldDate = DateUtils.truncate(lastSoldDate,
				Calendar.DAY_OF_MONTH);
	}

	public void setLastSoldDate(long lastSoldDate) {
		if (lastSoldDate > 0) {
			setLastSoldDate(new Date(lastSoldDate));
		} else {
			this.lastSoldDate = null;
		}
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

	public void setAcres(double acres) {
		this.acres = (float) acres;
	}

	public byte getBeds() {
		return beds;
	}

	public void setBeds(byte beds) {
		this.beds = beds;
	}

	public void setBeds(int beds) {
		this.beds = (byte) beds;
	}

	public float getBaths() {
		return baths;
	}

	public void setBaths(float baths) {
		this.baths = baths;
	}

	public void setBaths(double baths) {
		this.baths = (float) baths;
	}

	@Override
	public void readFields(DataInput dataInput) throws IOException {
		this.zpid = dataInput.readLong();
		this.latitude = dataInput.readInt();
		this.longitude = dataInput.readInt();
		this.soldPrice = dataInput.readInt();
		long tm = dataInput.readLong();
		if (tm > Long.MIN_VALUE) {
			this.setLastSoldDate(new Date(tm));
		} else {
			this.lastSoldDate=null;
		}
		
		this.yearBuilt = dataInput.readInt();
		this.acres = dataInput.readFloat();
		this.baths = dataInput.readFloat();
		this.beds = dataInput.readByte();
		this.squareFeet = dataInput.readInt();
		this.address = dataInput.readLine();
	}

	@Override
	public void write(DataOutput dataOutput) throws IOException {
		dataOutput.writeLong(this.zpid);
		dataOutput.writeInt(latitude);
		dataOutput.writeInt(longitude);
		dataOutput.writeInt(soldPrice);
		if (this.lastSoldDate !=null) {
			Calendar c = Calendar.getInstance();
			c.setTime(lastSoldDate);
			long tm =c.getTimeInMillis();
			dataOutput.writeLong(tm);
		} else {
			dataOutput.writeLong(Long.MIN_VALUE);
		}
		dataOutput.writeInt(yearBuilt);
		dataOutput.writeFloat(acres);
		dataOutput.writeFloat(baths);
		dataOutput.writeByte(beds);
		dataOutput.writeInt(squareFeet);
		dataOutput.writeBytes(address);
	}
}