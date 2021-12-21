package com.wisdge.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.CompilationMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.util.List;

public class JVMUtils {

	public static String getJVMInfo() {
		StringBuffer buffer = new StringBuffer();

		// Java 虚拟机线程系统的管理接口 ThreadMXBean
		ThreadMXBean th = ManagementFactory.getThreadMXBean();
		buffer.append("活动线程的当前数目: " + th.getThreadCount()).append("\n");
		buffer.append("活动守护线程的当前数目: " + th.getDaemonThreadCount()).append("\n");
		buffer.append("虚拟机启动或峰值重置以来峰值活动线程计数: " + th.getPeakThreadCount()).append("\n");
		buffer.append("当前线程的总 CPU时间（毫秒）: " + th.getCurrentThreadUserTime()).append("\n");
		buffer.append("当前线程在用户模式中执行的 CPU时间: " + th.getCurrentThreadUserTime()).append("\n\n");

		// Java 虚拟机的运行时系统的管理接口。 RuntimeMXBean
		RuntimeMXBean run = ManagementFactory.getRuntimeMXBean();
		buffer.append("虚拟机规范名称: " + run.getSpecName()).append("\n");
		buffer.append("当前虚拟机的名称: " + run.getName()).append("\n");
		buffer.append("系统类加载器用于搜索类文件的 Java类路径: " + run.getClassPath()).append("\n");
		buffer.append("JAVA库路径: " + run.getLibraryPath()).append("\n\n");

		// Java 虚拟机内存系统的管理接口。 MemoryMXBean
		MemoryMXBean mem = ManagementFactory.getMemoryMXBean();
		buffer.append("用于对象分配的堆的当前内存使用量: " + mem.getHeapMemoryUsage()).append("\n");
		buffer.append("虚拟机使用的非堆内存的当前内存使用量: " + mem.getNonHeapMemoryUsage()).append("\n");
		// Java 虚拟机的编译系统的管理接口 CompilationMXBean
		CompilationMXBean com = ManagementFactory.getCompilationMXBean();
		buffer.append("即时 (JIT)编译器的名称: " + com.getName()).append("\n");
		buffer.append("编译花费的累积耗费时间的近似值(毫秒): " + com.getTotalCompilationTime()).append("\n\n");

		// Java 虚拟机的类加载系统的管理接口 ClassLoadingMXBean
		ClassLoadingMXBean cl = ManagementFactory.getClassLoadingMXBean();
		buffer.append("当前加载到 虚拟机中的类的数量: " + cl.getLoadedClassCount()).append("\n");
		buffer.append("虚拟机开始执行到目前已经加载的类的总数: " + cl.getTotalLoadedClassCount()).append("\n");
		buffer.append("虚拟机开始执行到目前已经卸载的类的总数: " + cl.getUnloadedClassCount()).append("\n\n");

		// 用于操作系统的管理接口，Java 虚拟机在此操作系统上运行 OperatingSystemMXBean
		OperatingSystemMXBean op = ManagementFactory.getOperatingSystemMXBean();
		buffer.append("操作系统的架构: " + op.getArch()).append("\n");
		buffer.append("操作系统名称: " + op.getName()).append("\n");
		buffer.append("操作系统的版本: " + op.getVersion()).append("\n");
		buffer.append("虚拟机可以使用的处理器数目: " + op.getAvailableProcessors()).append("\n\n");

		buffer.append("文件编码： ").append(System.getProperty("file.encoding")).append("\n");
		buffer.append("文件编码类包：").append(System.getProperty("file.encoding.pkg")).append("\n");
		buffer.append("文件路径分割符： ").append(System.getProperty("file.separator")).append("\n");
		buffer.append("当前用户路径： ").append(System.getProperty("user.dir")).append("\n\n");

		// 内存池的管理接口。内存池表示由 Java 虚拟机管理的内存资源，
		// 由一个或多个内存管理器对内存池进行管理 MemoryPoolMXBean
		List<MemoryPoolMXBean> list = ManagementFactory.getMemoryPoolMXBeans();
		for (MemoryPoolMXBean mp : list) {
			buffer.append("虚拟机启动以来或自峰值重置以来此内存池的峰值内存使用量: " + mp.getPeakUsage()).append("\n");
			buffer.append("内存池的类型: " + mp.getType()).append("\n");
			buffer.append("内存使用量超过其阈值的次数: " + mp.getUsage()).append("\n\n");
		}

		return buffer.toString();
	}

	public static String getThrowableTrace(Throwable t) {
		StringWriter writer = new StringWriter();
		String message = "";
		try {
			t.printStackTrace(new PrintWriter(writer));
			message = writer.getBuffer().toString();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return message;
	}
}
