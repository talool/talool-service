package com.talool.image.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talool.service.ServiceConfig;
import com.talool.utils.SafeSimpleDateFormat;

/**
 * The class handles file and folder names
 * 
 * @author dmccuen
 *
 */
public class FileNameUtils
{
	private static final Logger LOG = LoggerFactory.getLogger(FileNameUtils.class);
	private static final String baseUrl = ServiceConfig.get().getStaticLogoBaseUrl();
	private static final String baseUploadDir = ServiceConfig.get().getUploadDir();
	private static final SafeSimpleDateFormat dateFormat = new SafeSimpleDateFormat("yyMMddHHmmss");

	public static String getImageUrl(java.io.File image, UUID merchantId)
	{
		StringBuilder sb = new StringBuilder(baseUrl);
		if (merchantId != null)
		{
			sb.append(getMerchantFolderName(merchantId))
					.append("/");
		}
		sb.append(image.getName());
		return sb.toString();
	}

	public static File getImageDir(UUID merchantId, boolean original) throws IOException
	{
		StringBuilder folderPath = new StringBuilder(baseUploadDir);
		folderPath.append("/").append(getMerchantFolderName(merchantId));

		if (original)
		{
			folderPath.append("/original");
		}

		File merchantFolder = new File(folderPath.toString());
		if (!merchantFolder.exists())
		{
			if (!merchantFolder.mkdirs()) 
			{
				String error = "Failed to create directory: "+folderPath.toString();
				LOG.error(error);
				throw new IOException(error);
			}
		}

		return merchantFolder;
	}
	
	/**
	 * @param imageUrl - a url that will be download to our servers
	 * @return The output file that will eventually store the downloaded media.  It will be an empty/new file when returned.
	 * @throws IOException
	 */
	public static File getFile(URL imageUrl) throws IOException
	{
		String imagePath = imageUrl.getPath();
		String[] segs = imagePath.split("/");
		String imageName = segs[segs.length-1];
		return getFile(imageName);
	}

	/**
	 * @param image - a that has been uploaded to our servers
	 * @return The output file that will eventually store the copied media.  It will be an empty/new file when returned.
	 * @throws IOException
	 */
	public static File getFile(File image) throws IOException
	{
		return getFile(image.getName());
	}
	
	/**
	 * @param imageName - the name of the file
	 * @return The output file that will eventually store the media.  It will be an empty/new file when returned.
	 * @throws IOException
	 */
	public static File getFile(String imageName) throws IOException
	{
		StringBuilder folderPath = new StringBuilder(baseUploadDir);
		folderPath.append("/");
		File folder = new File(folderPath.toString());
		return new File(folder, imageName);
	}

	/**
	 * @param image - an existing image file that has been uploaded or download to our servers
	 * @param merchantId - the UUID of the merchant is used for creating the new directory for this file
	 * @param original - original files are stored in a sub-directory of the merchant directory
	 * @return The output file that will eventually store the copied or processed media.  It will be an empty/new file when returned.
	 * @throws IOException
	 */
	public static File getOutputFile(File image, UUID merchantId, boolean original) throws IOException
	{
		File folder = getImageDir(merchantId, original);
		String name = (original) ? prefixUniqueAndClean(image.getName()) : getPngFileName(image);

		File f = new File(folder, name);

		return f;
	}

	private static String getMerchantFolderName(UUID merchantId)
	{
		return merchantId.toString();
	}

	/*
	 * Helps ensure we always save PNGs for display in the apps.
	 * We should avoid this method for original files.
	 */
	private static String getPngFileName(File image)
	{
		return getPngFileName(image.getName());
	}
	public static String getPngFileName(String imageName)
	{
		int dot = imageName.lastIndexOf(".");
		final StringBuilder sb = new StringBuilder();
		sb.append(prefixUniqueAndClean(imageName.substring(0, dot)));
		return sb.append(".png").toString();
	}
	
	public static String prefixUniqueAndClean(final String fileName)
	{
		// TODO refactor this so we can detect duplicate images
		// hash the filename (or full url) plus the size of the file.
		// actually, this should be used for originals, 
		// but getPngFileName should be used for all processed files.  
		// the hash should be generated before processing to check if the file already exists
		return dateFormat.format(System.currentTimeMillis()) + fileName.replaceAll(" ", "_");
	}
	
	/*
	 * TODO not using this now, consider using it later
	 * gets the filename for an image upload after processing by imagemagick... overkill? .. skip this?
	 */
	public static String getHashedFilename(File image, UUID merchantId)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(image.getName())
		  .append(image.length());
		return Base64.encodeBase64String(sb.toString().getBytes());
	}
	
	/*
	 * TODO not using this now
	 * gets the filename for a url download after processing by imagemagick... overkill? ... 
	 * just base64 the url when creating the file on download?  assume the 3rd party will use unique urls?
	 */
	public static String getHashedFilename(File image, URL imageUrl)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(imageUrl.getFile())
		  .append(image.length());
		return Base64.encodeBase64String(sb.toString().getBytes());
	}
}
