package com.talool.domain;

import com.talool.core.Image;

public class ImageImpl implements Image {
	
	private static final long serialVersionUID = -980003735981843715L;
	private String label;
	private String url;
	
	public ImageImpl(String label, String url) {
		this.label = label;
		this.url = url;
	}
	
	@Override
	public String getLabel() {
		return label;
	}
	@Override
	public void setLabel(String label) {
		this.label = label;
	}
	@Override
	public String getUrl() {
		return url;
	}
	@Override
	public void setUrl(String url) {
		this.url = url;
	}
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return null;
	}

	

}
