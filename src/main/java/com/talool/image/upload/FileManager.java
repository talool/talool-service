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
	
	public static int save(File image, URL imageUrl, UUID merchantId, boolean original) throws IOException
	{
		File imageFile = FileNameUtils.getOutputFile(image, imageUrl, merchantId, original);
		FileOutputStream fileOS = new FileOutputStream(imageFile, false);
		FileInputStream fileIS = new FileInputStream(image);
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
	public File process(File image, URL imageUrl, MediaType mediaType, UUID merchantId) throws IOException
	{
		// stash the original file
		save(image, imageUrl, merchantId, true);

		// process the file
		final AbstractMagick magick = (AbstractMagick)
				this.magickFactory.getMagickForMediaType(mediaType);
		magick.setInputFile(FileNameUtils.getFile(image));

		final File transformedOutFile =  FileNameUtils.getOutputFile(image, imageUrl, merchantId, false);
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
