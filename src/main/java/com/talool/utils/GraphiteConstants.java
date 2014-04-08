package com.talool.utils;

public class GraphiteConstants {
	
	public static final String any = "any";
	
	public enum Action {
		activity_action_taken, get_activities, authenticate, validate_code, get_deal_acquires, registration,
		redemption,  get_merchant_acquires, get_merchants, favorite, get_favorites, gift, get_gifts,
		purchase, password, fundraiser_purchase
	}
	
	public enum SubAction {
		facebook, email, user, merchant, merchant_code, add, remove, accept, reject,
		activate_code, create_reset, credit_card, credit_card_code, credit_wildcard, activation_code
	}
	
	public enum DeviceType
	{
		iphone, android
	}
	
	public enum Environment
	{
		production, development
	}
	
	public enum Apps
	{
		mobile
	}
	
	public enum WhiteLabel
	{
		core
	}
}
