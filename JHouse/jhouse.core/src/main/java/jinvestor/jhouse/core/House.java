package jinvestor.jhouse.core;

/*
 * #%L
 * jinvestor.parent
 * %%
 * Copyright (C) 2014 Michael Scott Knapp
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Date;


public class House {
	private final long zpid;
	private final String address;
	private final int squareFeet;
	private final int soldPrice;
	private final int latitude,longitude;
	private final Date lastSoldDate;
	private final int yearBuilt;
	private final float acres;

	// don't index on these, ever
	private final byte beds;
	private final float baths;
	
	private House(HouseBuilder builder) {
		this.zpid = builder.zpid;
		this.latitude=builder.latitude;
		this.longitude = builder.longitude;
		this.soldPrice = builder.soldPrice;
		this.lastSoldDate = builder.lastSoldDate;
		this.yearBuilt = builder.yearBuilt;
		this.acres = builder.acres;
		this.baths = builder.baths;
		this.beds = builder.beds;
		this.squareFeet = builder.squareFeet;
		this.address = builder.address;
	}
	
	public long getZpid() {
		return zpid;
	}

	public int getLatitude() {
		return latitude;
	}

	public int getLongitude() {
		return longitude;
	}

	public int getSoldPrice() {
		return soldPrice;
	}

	public Date getLastSoldDate() {
		return lastSoldDate;
	}

	public int getYearBuilt() {
		return yearBuilt;
	}

	public float getAcres() {
		return acres;
	}

	public byte getBeds() {
		return beds;
	}

	public float getBaths() {
		return baths;
	}

	public int getSquareFeet() {
		return squareFeet;
	}

	public String getAddress() {
		return address;
	}

	public static HouseBuilder builder() {
		return new HouseBuilder();
	}

	public static HouseBuilder builder(House house) {
		return new HouseBuilder(house);
	}

	public static HouseBuilder builder(HouseBuilder house) {
		return new HouseBuilder(house);
	}
	
	public static House fromEntity(final HouseEntity entity) {
		return new HouseBuilder(entity).build();
	}
	
	public HouseBuilder cloneToBuilder() {
		return new HouseBuilder(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (zpid ^ (zpid >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		House other = (House) obj;
		if (zpid != other.zpid)
			return false;
		return true;
	}

	public boolean strictlyEquals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		House other = (House) obj;
		if (Float.floatToIntBits(acres) != Float.floatToIntBits(other.acres))
			return false;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (Float.floatToIntBits(baths) != Float.floatToIntBits(other.baths))
			return false;
		if (beds != other.beds)
			return false;
		if (lastSoldDate == null) {
			if (other.lastSoldDate != null)
				return false;
		} else if (!lastSoldDate.equals(other.lastSoldDate))
			return false;
		if (latitude != other.latitude)
			return false;
		if (longitude != other.longitude)
			return false;
		if (soldPrice != other.soldPrice)
			return false;
		if (squareFeet != other.squareFeet)
			return false;
		if (yearBuilt != other.yearBuilt)
			return false;
		if (zpid != other.zpid)
			return false;
		return true;
	}

	public static class HouseBuilder {
		private long zpid;
		private int latitude,longitude;
		private int soldPrice;
		private Date lastSoldDate;
		private int yearBuilt;
		private float acres;
		private byte beds;
		private float baths;
		private int squareFeet;
		private String address;
		
		public HouseBuilder(){}
		
		public HouseBuilder(House h) {
			this.zpid = h.zpid;
			this.lastSoldDate = h.lastSoldDate;
			this.acres = h.acres;
			this.address = h.address;
			this.baths = h.baths;
			this.beds = h.beds;
			this.latitude = h.latitude;
			this.longitude = h.longitude;
			this.soldPrice = h.soldPrice;
			this.squareFeet = h.squareFeet;
		}
		
		public HouseBuilder(HouseBuilder h) {
			this.zpid = h.zpid;
			this.lastSoldDate = h.lastSoldDate;
			this.acres = h.acres;
			this.address = h.address;
			this.baths = h.baths;
			this.beds = h.beds;
			this.latitude = h.latitude;
			this.longitude = h.longitude;
			this.soldPrice = h.soldPrice;
			this.squareFeet = h.squareFeet;
		}
		
		public HouseBuilder(HouseEntity house) {
			this.zpid = house.getZpid();
			this.latitude=house.getLatitude();
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
		
		public HouseBuilder zpid(long zpid) {
			this.zpid = zpid;
			return this;
		}
		public HouseBuilder lastSoldDate(Date lastSoldDate) {
			this.lastSoldDate = lastSoldDate;
			return this;
		}
		public HouseBuilder acres(float acres) {
			this.acres = acres;
			return this;
		}
		public HouseBuilder beds(int beds) {
			this.beds = (byte)beds;
			return this;
		}
		public HouseBuilder baths(float baths) {
			this.baths = baths;
			return this;
		}
		public HouseBuilder latitude(int latitude) {
			this.latitude = latitude;
			return this;
		}
		public HouseBuilder longitude(int longitude) {
			this.longitude = longitude;
			return this;
		}
		public HouseBuilder soldPrice(int soldPrice) {
			this.soldPrice = soldPrice;
			return this;
		}
		public HouseBuilder yearBuilt(int yearBuilt) {
			this.yearBuilt = yearBuilt;
			return this;
		}
		public HouseBuilder squareFeet(int squareFeet) {
			this.squareFeet = squareFeet;
			return this;
		}
		public HouseBuilder address(String address) {
			this.address = address;
			return this;
		}
		public House build() {
			return new House(this);
		}
	}
}
/*
<article id="zpid_36197475" statustype="3" role="article" class="property-listing property-listing-large rollable plisting" longitude="-76742627" latitude="39269820">
<figure class="photo left column property-thumb " role="img" data-photourl="http://dev.virtualearth.net/REST/v1/Imagery/Map/Aerial/39.269820,-76.742627/17?mapSize=150,112&amp;key=AmdvKO2hNoyLePakiVzRBZiGPKCxV6MtwWneohoPfmXclTaRTzvT6_Ict5-PBHO6">
 <a href="/homedetails/1206-Frederick-Rd-Baltimore-MD-21228/36197475_zpid/" class="routable mask hdp-link"><img class="image-loaded" rel="nofollow" src="http://dev.virtualearth.net/REST/v1/Imagery/Map/Aerial/39.269820,-76.742627/17?mapSize=150,112&amp;key=AmdvKO2hNoyLePakiVzRBZiGPKCxV6MtwWneohoPfmXclTaRTzvT6_Ict5-PBHO6" alt="1206 Frederick Rd, Baltimore, MD" /></a>
</figure>
<!-- Property Details -->
<div class="right property-info">
 <dl class="property-info-list col-1 column">
  <dt class="type-recentlySold type show-icon">
   <div class="type-icon"></div>
   <strong>Sold: $335,000</strong>
  </dt>
  <dt class="sold-date">
   Sold on 1/29/14
  </dt>
  <div class="colabc">
   <div class="zestimate">
    <span class="right-align definition yui3-tooltip-trigger" data-tooltip-key="zest-tip-list">Zestimate</span>
    <sup>&reg;</sup>: $319K
   </div>
  </div>
  <dt class="price-sqft-a">
   Price/sqft: 
  </dt>
  <dd class="price-sqft-b">
   $93
  </dd>
 </dl>
 <dl class="property-info-list col-2 column">
  <dt class="property-address">
   <a href="/homedetails/1206-Frederick-Rd-Baltimore-MD-21228/36197475_zpid/" class="hdp-link routable" title="1206 Frederick Rd, Baltimore, MD Real Estate">1206 Frederick Rd, Baltimore, MD</a>
  </dt>
  <dt class="property-data">
   4 beds, 3.0 baths, 3,590 sqft
  </dt>
  <dt class="property-lot">
   0.29 ac lot 
  </dt>
  <dt class="property-year">
    Built in 1957
  </dt>
 </dl>
</div>
<div class="minibubble template hide">
 &nbsp;
 <!--["$335,000","",4,3.0,3590,null]-->
</div>
</article>
*/