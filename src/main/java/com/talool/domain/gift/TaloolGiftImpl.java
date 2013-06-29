package com.talool.domain.gift;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.talool.core.gift.TaloolGift;

/**
 * 
 * @author clintz
 * 
 */
@Entity
@Table(name = "gift")
@DiscriminatorValue("T")
public class TaloolGiftImpl extends GiftImpl implements TaloolGift
{
	private static final long serialVersionUID = -6197930806091256725L;

}
