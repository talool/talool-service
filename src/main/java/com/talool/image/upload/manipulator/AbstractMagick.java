package com.talool.image.upload.manipulator;

import java.io.File;
import java.io.IOException;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.core.Info;
import org.im4java.core.InfoException;
import org.im4java.process.ProcessStarter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talool.image.upload.FileNameUtils;
import com.talool.service.ServiceConfig;

public abstract class AbstractMagick implements IMagick
{
	private static final Logger LOG = LoggerFactory.getLogger(AbstractMagick.class);
	private File inputFile;
	private File outputFile;
	
	protected static final int maxLogoWidth = 140;
	protected static final int maxLogoHeight = 40;
	
	private static final String baseUploadDir = ServiceConfig.get().getUploadDir();
	private static final String gradientFileName = "grayGradient4.png";

	static
	{
		ProcessStarter.setGlobalSearchPath(ServiceConfig.get().getImageMagickPath());
	}

	public File getInputFile()
	{
		return inputFile;
	}

	public void setInputFile(File inputFile)
	{
		this.inputFile = inputFile;
	}
	

	public File getOutputFile()
	{
		return outputFile;
	}

	public void setOutputFile(File outputFile)
	{
		this.outputFile = outputFile;
	}

	@Override
	public void process()
	{

		String inputFilePath = getInputFile().getAbsolutePath();
		String outputFilePath = getOutputFile().getAbsolutePath();

		ConvertCmd cmd = new ConvertCmd();
		try
		{
			WizardApprentice image = new WizardApprentice();
			if (image.isEPS)
			{
				//convert it to a PNG first
				inputFilePath = convertEPS();
			}
			
			// execute the operation
			cmd.run(getOperation(),inputFilePath,outputFilePath);
			
			debug();
		}
		catch (IM4JavaException ime)
		{
			LOG.error("failed to process image: ", ime);
		}
		catch (Exception e)
		{
			LOG.error("failed to process image: ", e);
		}

	}

	protected String getGradientFilePath()
	{
		StringBuilder filepath = new StringBuilder(baseUploadDir);
		filepath.append(gradientFileName);
		String gradientPath = filepath.toString();

		/*
		 * Check out this IM KungFu!!!
		 */
		File gradient = new File(gradientPath);
		if (!gradient.exists())
		{
			LOG.error("The PGN gradient is missing.  Need to create it.");
			// create this file if it doesn't exist
			ConvertCmd cmd = new ConvertCmd();
			IMOperation op = new IMOperation();
			op.size(10, 100);
			op.addImage("gradient:gray70-white", gradientPath);
			try
			{
				// execute the operation
				cmd.run(op);
				LOG.error("The PGN gradient has been created.");
			}
			catch (Exception e)
			{
				LOG.error("failed to generate the gradient: ", e);
			}
		}

		return gradientPath;
	}
	
	private String convertEPS() throws IM4JavaException, InterruptedException, IOException
	{
		ConvertCmd cmd = new ConvertCmd();
		IMOperation op = new IMOperation();

		op.colorspace("RGB");
		op.flatten();
		op.addImage();
		op.addImage();
		
		// execute the operation
		String filename = FileNameUtils.getPngFileName(getInputFile(), null);
		String inputFilePath = getInputFile().getAbsolutePath();
		StringBuilder sb = new StringBuilder(inputFilePath.substring(0,inputFilePath.lastIndexOf("/")));
		String tempFilePath = sb.append("/").append(filename).toString();
		cmd.run(op,inputFilePath,tempFilePath);
		
		return tempFilePath;	
	}

	private void debug()
	{
		StringBuilder sb = new StringBuilder("IMOperation: convert ");
		sb.append(getOperation().toString());
		LOG.debug(sb.toString());
	}
	
	protected class WizardApprentice {
		public boolean isRGB;
		public boolean isJPEG;
		public boolean isTooBig;
		public boolean hasAlpha;
		public boolean isEPS;
		
		public WizardApprentice()
		{
			try
			{
				Info info = new Info(getInputFile().getAbsolutePath());
				String colorspace = info.getProperty("Colorspace");
				String alpha = info.getProperty("Alpha");
				
				isTooBig = (info.getImageWidth() > maxLogoWidth || info.getImageHeight() > maxLogoHeight);
				isRGB = !colorspace.equalsIgnoreCase("cmyk");
				isJPEG = info.getImageFormat().contains("JPEG");
				isEPS = info.getImageFormat().contains("PostScript");
				hasAlpha = (alpha!=null);
				
				LOG.info(info.getImageFormat());
				//LOG.info(colorspace);
				//LOG.info(alpha);
			}
			catch(InfoException ie)
			{
				LOG.debug("failed to get info on image.", ie);
			}
		}
	}

}
