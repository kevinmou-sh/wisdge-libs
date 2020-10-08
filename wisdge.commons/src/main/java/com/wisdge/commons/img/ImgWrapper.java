package com.wisdge.commons.img;

import com.wisdge.utils.FileUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片转换的操作类 (裁剪+旋转+伸缩+水印+边框+)
 */
public class ImgWrapper {
	private static final Logger logger = LoggerFactory.getLogger(ImgWrapper.class);
	
	/**
	 * 根据本地图片进行处理
	 */
	public static Builder of(BufferedImage bufferedImage) {
		return of(bufferedImage, "JPEG");
	}
	public static Builder of(BufferedImage bufferedImage, String formatName) {
		if (bufferedImage == null)
			throw new NullPointerException("Cannot specify null for input file.");
		
		return Builder.of(bufferedImage, formatName);
	}

	public static class Builder {
		private BufferedImage source;
		private String formatName;
		private List<Operate> operates = new ArrayList<>();

		public Builder(BufferedImage source, String formatName) {
			this.source = source;
			this.formatName = formatName;
		}

		private static Builder of(BufferedImage bufferedImage, String formatName) {
			return new Builder(bufferedImage, formatName);
		}

		/**
		 * 缩放
		 *
		 * @param width
		 * @param height
		 * @return
		 */
		public Builder scale(Integer width, Integer height, Integer quality) {
			return scale(width, height, quality, false);
		}

		public Builder scale(Integer width, Integer height, Integer quality, boolean forceScale) {
			Operate operate = new Operate();
			operate.setOperateType(OperateType.SCALE);
			operate.setWidth(width);
			operate.setHeight(height);
			operate.setQuality(quality);
			operate.setForceScale(forceScale);
			operates.add(operate);
			return this;
		}

		/**
		 * 按照比例进行缩放
		 *
		 * @param radio 1.0 表示不缩放, 0.5 缩放为一半
		 * @return
		 */
		public Builder scale(Double radio, Integer quality) {
			Operate operate = new Operate();
			operate.setOperateType(OperateType.SCALE);
			operate.setRadio(radio);
			operate.setQuality(quality);
			operates.add(operate);
			return this;
		}

		/**
		 * 裁剪
		 *
		 * @param x
		 * @param y
		 * @param width
		 * @param height
		 * @return
		 */
		public Builder crop(int x, int y, int width, int height) {
			Operate operate = new Operate();
			operate.setOperateType(OperateType.CROP);
			operate.setWidth(width);
			operate.setHeight(height);
			operate.setX(x);
			operate.setY(y);
			operates.add(operate);
			return this;
		}

		/**
		 * 旋转
		 *
		 * @param rotate
		 * @return
		 */
		public Builder rotate(double rotate) {
			Operate operate = new Operate();
			operate.setOperateType(OperateType.ROTATE);
			operate.setRotate(rotate);
			operates.add(operate);
			return this;
		}

		/**
		 * 上下翻转
		 *
		 * @return
		 */
		public Builder flip() {
			Operate operate = new Operate();
			operate.setOperateType(OperateType.FLIP);
			operates.add(operate);
			return this;
		}

		/**
		 * 左右翻转,即镜像
		 *
		 * @return
		 */
		public Builder flop() {
			Operate operate = new Operate();
			operate.setOperateType(OperateType.FLOP);
			operates.add(operate);
			return this;
		}

		/**
		 * 添加边框
		 *
		 * @param width  边框的宽
		 * @param height 边框的高
		 * @param color  边框的填充色
		 * @return
		 */
		public Builder board(Integer width, Integer height, String color) {
			Operate args = new Operate();
			args.setOperateType(OperateType.BOARD);
			args.setWidth(width);
			args.setHeight(height);
			args.setColor(color);
			operates.add(args);
			return this;
		}

		/**
		 * 执行图片处理, 并保存文件为: 源文件_out.jpg （类型由输出的图片类型决定）
		 *
		 * @return 保存的文件名
		 * @throws Exception
		 */
		public String toFile() throws Exception {
			return toFile(null);
		}

		/**
		 * 执行图片处理,并将结果保存为指定文件名的file
		 *
		 * @param outputFilename 若为null, 则输出文件为 源文件_out.jpg 这种格式
		 * @return
		 * @throws Exception
		 */
		public String toFile(String outputFilename) throws Exception {
			if (CollectionUtils.isEmpty(operates)) {
				throw new ImgOperateException("Operates null!");
			}

			File sourceFile = File.createTempFile("imgWrapper", "." + formatName);
			ImageIO.write(source, formatName, sourceFile);
			String sourceFilename = sourceFile.getAbsolutePath();
			logger.debug("Image save to temporary file: {}", sourceFile.getAbsoluteFile());
			if (outputFilename == null)
				outputFilename = FilenameUtils.concat(FilenameUtils.getFullPath(sourceFilename), FilenameUtils.removeExtension(sourceFile.getName()) + "_out." + formatName);
			logger.debug("Image wrapper to temporary file: {}", outputFilename);

			/** 执行图片的操作 */
			ImgBaseOperate.operate(operates, sourceFilename, outputFilename);
			return outputFilename;
		}

		/**
		 * 执行图片操作,并输出字节流
		 *
		 * @return
		 * @throws Exception
		 */
		public InputStream asStream() throws Exception {
			if (CollectionUtils.isEmpty(operates)) {
				throw new ImgOperateException("Operate null!");
			}

			return new FileInputStream(new File(this.toFile()));
		}

		public byte[] asBytes() throws Exception {
			if (CollectionUtils.isEmpty(operates)) {
				throw new ImgOperateException("Operate null!");
			}

			return FileUtils.readFileToByteArray(new File(this.toFile()));
		}

		public BufferedImage asImg() throws Exception {
			if (CollectionUtils.isEmpty(operates)) {
				throw new ImgOperateException("Operate null!");
			}

			return ImageIO.read(new File(this.toFile()));
		}

		public static class Operate {
			/**
			 * 操作类型
			 */
			private OperateType operateType;

			/**
			 * 裁剪宽; 缩放宽
			 */
			private Integer width;
			/**
			 * 高
			 */
			private Integer height;
			/**
			 * 裁剪时,起始 x
			 */
			private Integer x;
			/**
			 * 裁剪时,起始y
			 */
			private Integer y;
			/**
			 * 旋转角度
			 */
			private Double rotate;

			/**
			 * 按照整体的缩放参数, 1 表示不变, 和裁剪一起使用
			 */
			private Double radio;

			/**
			 * 图片精度, 1 - 100
			 */
			private Integer quality;

			/**
			 * 颜色 (添加边框中的颜色; 去除图片中某颜色)
			 */
			private String color;

			/**
			 * 水印图片的类型
			 */
			private String waterImgType;

			/**
			 * 强制按照给定的参数进行压缩
			 */
			private boolean forceScale;
			
			public OperateType getOperateType() {
				return operateType;
			}

			public void setOperateType(OperateType operateType) {
				this.operateType = operateType;
			}

			public Integer getWidth() {
				return width;
			}

			public void setWidth(Integer width) {
				this.width = width;
			}

			public Integer getHeight() {
				return height;
			}

			public void setHeight(Integer height) {
				this.height = height;
			}

			public Integer getX() {
				return x;
			}

			public void setX(Integer x) {
				this.x = x;
			}

			public Integer getY() {
				return y;
			}

			public void setY(Integer y) {
				this.y = y;
			}

			public Double getRotate() {
				return rotate;
			}

			public void setRotate(Double rotate) {
				this.rotate = rotate;
			}

			public Double getRadio() {
				return radio;
			}

			public void setRadio(Double radio) {
				this.radio = radio;
			}

			public Integer getQuality() {
				return quality;
			}

			public void setQuality(Integer quality) {
				this.quality = quality;
			}

			public String getColor() {
				return color;
			}

			public void setColor(String color) {
				this.color = color;
			}

			public String getWaterImgType() {
				return waterImgType;
			}

			public void setWaterImgType(String waterImgType) {
				this.waterImgType = waterImgType;
			}

			public boolean isForceScale() {
				return forceScale;
			}

			public void setForceScale(boolean forceScale) {
				this.forceScale = forceScale;
			}

			public boolean valid() {
				switch (operateType) {
				case CROP:
					return width != null && height != null && x != null && y != null;
				case SCALE:
					return width != null || height != null || radio != null;
				case ROTATE:
					return rotate != null;
				case BOARD:
					if (width == null) {
						width = 3;
					}
					if (height == null) {
						height = 3;
					}
					if (color == null) {
						color = "#ffffff";
					}
				case FLIP:
				case FLOP:
					return true;
				default:
					return false;
				}
			}
		}

		public enum OperateType {
			/**
			 * 裁剪
			 */
			CROP,
			/**
			 * 缩放
			 */
			SCALE,
			/**
			 * 旋转
			 */
			ROTATE,
			/**
			 * 水印
			 */
			WATER,
			/**
			 * 上下翻转
			 */
			FLIP,
			/**
			 * 水平翻转
			 */
			FLOP,
			/**
			 * 添加边框
			 */
			BOARD;
		}
	}
}