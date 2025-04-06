package com.scb.util;


import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;

public class SFTPUtils {
    private static final Logger logger = LoggerFactory.getLogger(SFTPUtils.class);

    public static boolean copyFromSftp(String server, int port, String user,
                                      String privateKeyPath, String passphrase,
                                      String remoteFilePath, String localFilePath) {
        Session session = null;
        ChannelSftp channelSftp = null;

        try {
            JSch jsch = new JSch();
            logger.debug("Initializing JSch SFTP client");

            // Add private key
            if (passphrase != null && !passphrase.isEmpty()) {
                jsch.addIdentity(privateKeyPath, passphrase);
                logger.debug("Added private key with passphrase protection");
            } else {
                jsch.addIdentity(privateKeyPath);
                logger.debug("Added private key without passphrase");
            }

            // Configure session
            session = jsch.getSession(user, server, port);
            session.setConfig("StrictHostKeyChecking", "no");
            logger.info("Connecting to {}@{}:{}", user, server, port);
            session.connect();

            // Open SFTP channel
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            logger.debug("SFTP channel opened");

            // Ensure local directory exists
            File localFile = new File(localFilePath);
            File parentDir = localFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                logger.info("Creating local directory: {}", parentDir.getAbsolutePath());
                parentDir.mkdirs();
            }

            // Download file
            logger.info("Downloading {} to {}", remoteFilePath, localFilePath);
            channelSftp.get(remoteFilePath, localFilePath);
            logger.info("File downloaded successfully");
            return true;
        } catch (JSchException | SftpException e) {
            logger.error("SFTP operation failed: {}", e.getMessage(), e);
            return false;
        } finally {
            if (channelSftp != null && channelSftp.isConnected()) {
                channelSftp.exit();
                logger.debug("SFTP channel closed");
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
                logger.debug("SSH session disconnected");
            }
        }
    }
}