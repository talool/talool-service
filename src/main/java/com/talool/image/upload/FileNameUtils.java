package com.talool.image.upload;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talool.core.MerchantMedia;
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
	 * @param imageUrl - the used to download the image (can be null)
	 * @param merchantId - the UUID of the merchant is used for creating the new directory for this file
	 * @param original - original files are stored in a sub-directory of the merchant directory
	 * @return The output file that will eventually store the copied or processed media.  It will be an empty/new file when returned.
	 * @throws IOException
	 */
	public static File getOutputFile(File image, URL imageUrl, UUID merchantId, boolean original) throws IOException
	{
		File folder = getImageDir(merchantId, original);
		String name = (original) ? prefixUniqueAndClean(image.getName()) : getPngFileName(image,imageUrl);

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
	public static String getPngFileName(File image, URL imageUrl)
	{
		final StringBuilder sb = new StringBuilder();
		String hashImageName = (imageUrl==null) ? getHashedFilename(image):getHashedFilename(image.length(), imageUrl);
		sb.append(prefixUniqueAndClean(hashImageName));
		return sb.append(".png").toString();
	}
	
	public static String prefixUniqueAndClean(final String hashedFileName)
	{
		return dateFormat.format(System.currentTimeMillis()) + "_" + hashedFileName;
	}
	
	/*
	 * Gets the filename for saving an image after processing by imagemagick.
	 * Used for uploaded images.
	 */
	public static String getHashedFilename(File savedImage)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(savedImage.getName())
		  .append(savedImage.length());
		StringBuilder sb2 = new StringBuilder();
		sb2.append(sb.toString().hashCode());
		return sb2.toString();
	}
	
	/*
	 * Gets the filename for saving an image after processing by imagemagick.
	 * Used for downloaded images.
	 */
	public static String getHashedFilename(long fileSize, URL imageUrl)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(imageUrl.getFile())
		  .append(fileSize);
		StringBuilder sb2 = new StringBuilder();
		sb2.append(sb.toString().hashCode());
		return sb2.toString();
	}
	
	private static MerchantMedia getExistingMedia(List<MerchantMedia> possibleMatches, String searchString)
	{
		MerchantMedia matchedMedia = null;
		for (MerchantMedia media:possibleMatches)
		{
			String hashedFilenameWithUniqueness = media.getMediaName();
			String[] segs = hashedFilenameWithUniqueness.split("_");
			if (segs.length == 2)
			{
				if (segs[1].equals(searchString))
				{
					matchedMedia = media;
					break;
				}
			}
		}
		return matchedMedia;
	}
	
	public static MerchantMedia getExistingMedia(List<MerchantMedia> possibleMatches, File savedImage)
	{
		String searchString = getHashedFilename(savedImage) + ".png";
		return getExistingMedia(possibleMatches, searchString);
	}
	
	public static MerchantMedia getExistingMedia(List<MerchantMedia> possibleMatches, File savedImage, URL imageUrl)
	{
		String searchString = getHashedFilename(savedImage.length(), imageUrl) + ".png";
		return getExistingMedia(possibleMatches, searchString);
	}
	
	public static MerchantMedia getExistingMedia(List<MerchantMedia> possibleMatches, long fileSize, URL imageUrl)
	{
		String searchString = getHashedFilename(fileSize, imageUrl) + ".png";
		return getExistingMedia(possibleMatches, searchString);
	}
}
