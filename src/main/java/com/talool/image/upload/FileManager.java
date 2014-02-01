package com.talool.image.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

import org.apache.commons.io.IOUtils;

import com.talool.core.MediaType;
import com.talool.image.upload.manipulator.AbstractMagick;
import com.talool.image.upload.manipulator.IMagickFactory;
import com.talool.image.upload.manipulator.MagickFactory;

/**
 * A simple file manager that knows how to store, read and delete files from the
 * file system.
 */
public class FileManager
{
	private final File baseFolder;
	private final IMagickFactory magickFactory;

	public FileManager(final String baseFolder)
	{
		this.baseFolder = new File(baseFolder);
		this.magickFactory = new MagickFactory();
	}

	public int save(File fileItem) throws IOException
	{
		File file = new File(baseFolder, fileItem.getName());
		FileOutputStream fileOS = new FileOutputStream(file, false);
		FileInputStream fileIS = new FileInputStream(fileItem);
		return IOUtils.copy(fileIS, fileOS);
	}
	
	public int save(URL imageUrl) throws IOException
	{
		File imageFile = FileNameUtils.getFile(imageUrl);
		FileOutputStream fileOS = new FileOutputStream(imageFile, false);
		InputStream fileIS = imageUrl.openStream();
		return IOUtils.copy(fileIS, fileOS);
	}
	
	/**
	 * 
	 * @param image
	 * @param mediaType
	 * @param merchantId
	 * @return The output file created
	 * @throws IOException
	 */
	public File process(URL imageUrl, MediaType mediaType, UUID merchantId) throws IOException
	{
		// save it to the baseFolder
		save(imageUrl);
		
		// get the file for the saved image
		File image = FileNameUtils.getFile(imageUrl);
		
		// debugging
		// System.out.println("Saved image from url: "+imageUrl.getProtocol()+"://"+imageUrl.getHost()+imageUrl.getFile());
		// System.out.println("      image size in bytes: "+image.length());
		// System.out.println("      image location: "+image.getAbsolutePath());
		// System.out.println("      ");
		
		// process the file
		return process(image, mediaType, merchantId);
	}

	/**
	 * 
	 * @param image
	 * @param mediaType
	 * @param merchantId
	 * @return The output file created
	 * @throws IOException
	 */
	public File process(File image, MediaType mediaType, UUID merchantId) throws IOException
	{
		// stash the original file
		FileNameUtils.saveImage(image, merchantId, true);

		// write it to the base folder
		// TODO the file should have already been saved to the base folder.  need to check website uploads
		// TODO need to figure out why this class has save methods and FileUploadUtils does too
		//save(image);

		// process the file
		final AbstractMagick magick = (AbstractMagick)
				this.magickFactory.getMagickForMediaType(mediaType);
		magick.setInputFile(FileNameUtils.getFile(image));

		final File transformedOutFile = FileNameUtils.getOutputFile(image, merchantId, false);
		magick.setOutputFile(transformedOutFile);

		magick.process();

		// clean up the base folder
		delete(image.getName());

		return transformedOutFile;

	}

	// NOTE: we're not using this, but it is broken when "process" is used
	public byte[] get(String fileName) throws IOException
	{
		File file = new File(baseFolder, fileName);
		return IOUtils.toByteArray(new FileInputStream(file));
	}

	// This only deletes files from the baseFolder
	public boolean delete(String fileName)
	{
		File file = new File(baseFolder, fileName);
		if (file.exists()) return file.delete();
		return true;
	}
}
