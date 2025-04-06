package com.scb.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

public class FTPUtils {

	// Copy file FROM FTP server to local (download)
	public static boolean copyFromFtp(String server, int port, String user, String pass, String remoteFilePath,
			String localFilePath) {
		FTPClient ftpClient = new FTPClient();
		try {
			// Connect and authenticate
			ftpClient.connect(server, port);
			if (!ftpClient.login(user, pass)) {
				System.err.println("FTP login failed.");
				return false;
			}

			// Configure connection
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

			// Ensure local directory structure exists
			File localFile = new File(localFilePath);
			File parentDir = localFile.getParentFile();
			if (parentDir != null && !parentDir.exists()) {
				parentDir.mkdirs();
			}

			// Download file
			try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(localFile))) {
				return ftpClient.retrieveFile(remoteFilePath, outputStream);
			}
		} catch (IOException e) {
			System.err.println("FTP download error: " + e.getMessage());
			return false;
		} finally {
			disconnect(ftpClient);
		}
	}

	// Move file TO FTP server (upload without deleting local file)
	public static boolean moveToFtp(String server, int port, String user, String pass, String localFilePath,
			String remoteFilePath) {
		FTPClient ftpClient = new FTPClient();
		try {
			// Connect and authenticate
			ftpClient.connect(server, port);
			if (!ftpClient.login(user, pass)) {
				System.err.println("FTP login failed.");
				return false;
			}

			// Configure connection
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

			// Upload file
			try (InputStream inputStream = new BufferedInputStream(new FileInputStream(localFilePath))) {
				return ftpClient.storeFile(remoteFilePath, inputStream);
			}
		} catch (IOException e) {
			System.err.println("FTP upload error: " + e.getMessage());
			return false;
		} finally {
			disconnect(ftpClient);
		}
	}

	// Common disconnect method to avoid code duplication
	private static void disconnect(FTPClient ftpClient) {
		try {
			if (ftpClient.isConnected()) {
				ftpClient.logout();
				ftpClient.disconnect();
			}
		} catch (IOException e) {
			System.err.println("FTP disconnect error: " + e.getMessage());
		}
	}
	
//	public static void main(String[] args) {
//        // Download file
//        boolean downloaded = FTPUtils.copyFromFtp(
//            "ftp.example.com", 21, "user", "pass",
//            "/remote/file.txt", "/local/downloads/file.txt"
//        );
//
//        // Upload file (keeps local copy)
//        boolean uploaded = FTPUtils.moveToFtp(
//            "ftp.example.com", 21, "user", "pass",
//            "/local/uploads/file.txt", "/remote/file.txt"
//        );
//    }
}