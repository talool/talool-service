package com.talool.hibernate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.hibernate.transform.ResultTransformer;

import com.talool.core.Category;
import com.talool.core.Merchant;
import com.talool.core.MerchantMedia;
import com.talool.core.service.ServiceException;
import com.talool.domain.MerchantImpl;
import com.talool.domain.MerchantLocationImpl;
import com.talool.domain.MerchantMediaImpl;
import com.talool.service.ServiceFactory;
import com.vividsolutions.jts.geom.Geometry;

/**
 * 
 * @author clintz
 * 
 */
public class MerchantAcquiresResultTransformer implements ResultTransformer
{
	private final Map<UUID, MerchantImpl> merchantMap = new HashMap<UUID, MerchantImpl>();
	private final List<Merchant> merchants = new ArrayList<Merchant>();

	private static final long serialVersionUID = 1L;

	@Override
	public Object transformTuple(Object[] tuple, String[] aliases)
	{
		final UUID merchantId = (UUID) tuple[0];
		final String merchantName = (String) tuple[1];
		final Integer categoryId = (Integer) tuple[2];

		final Long locationId = (Long) tuple[3];
		final String locationName = (String) tuple[4];
		final String email = (String) tuple[5];
		final String websiteUrl = (String) tuple[6];
		final String phone = (String) tuple[7];
		final String address1 = (String) tuple[8];
		final String address2 = (String) tuple[9];
		final String city = (String) tuple[10];
		final String state = (String) tuple[11];
		final String zip = (String) tuple[12];
		final String country = (String) tuple[13];
		final Geometry point = (Geometry) tuple[14];

		final String merchantLogo = (String) tuple[15];
		final String merchantImage = (String) tuple[16];
		final Double distanceInMeters = (Double) tuple[17];

		final MerchantLocationImpl location = new MerchantLocationImpl();
		location.setId(locationId);
		location.setLocationName(locationName);
		location.setEmail(email);
		location.setWebsiteUrl(websiteUrl);
		location.setPhone(phone);
		location.setAddress1(address1);
		location.setAddress2(address2);
		location.setCity(city);
		location.setStateProvinceCounty(state);
		location.setZip(zip);
		location.setCountry(country);
		location.setGeometry(point);

		location.setDistanceInMeters(distanceInMeters);

		MerchantImpl merchant = merchantMap.get(merchantId);

		if (merchant == null)
		{
			merchant = new MerchantImpl();

			merchant.setName(merchantName);
			merchant.setId(merchantId);

			// category is cached
			try
			{
				Category cat = ServiceFactory.get().getTaloolService().getCategory(categoryId);
				merchant.setCategory(cat);
			}
			catch (ServiceException e)
			{
				e.printStackTrace();
			}

			final MerchantMedia logo = new MerchantMediaImpl();
			logo.setMediaUrl(merchantLogo);
			location.setLogo(logo);

			final MerchantMedia mercImg = new MerchantMediaImpl();
			mercImg.setMediaUrl(merchantImage);
			location.setMerchantImage(mercImg);

			merchant.getLocations().add(location);

			merchantMap.put(merchantId, merchant);
			merchants.add(merchant);
		}
		else
		{
			merchant.getLocations().add(location);
		}

		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List transformList(List collection)
	{
		return merchants;
	}
}
