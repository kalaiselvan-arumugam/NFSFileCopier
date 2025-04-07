package com.scb.util;

import com.jcraft.jsch.*;
import java.io.*;

public class SFTPUtils {

    // Copy file FROM SFTP server to local (download)
    public static boolean copyFromSftp(String server, int port, String user,
                                      String privateKeyPath, String passphrase,
                                      String remoteFilePath, String localFilePath) {
        Session session = null;
        ChannelSftp channelSftp = null;

        try {
            JSch jsch = new JSch();

            // Add private key (with optional passphrase)
            if (passphrase != null && !passphrase.isEmpty()) {
                jsch.addIdentity(privateKeyPath, passphrase);
            } else {
                jsch.addIdentity(privateKeyPath);
            }

            // Configure session
            session = jsch.getSession(user, server, port);
            session.setConfig("StrictHostKeyChecking", "no"); // For simplicity (use "yes" in prod)
            session.connect();

            // Open SFTP channel
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();

            // Ensure local directory exists
            File localFile = new File(localFilePath);
            File parentDir = localFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            // Download file
            channelSftp.get(remoteFilePath, localFilePath);
            return true;
        } catch (JSchException | SftpException e) {
            System.err.println("SFTP error: " + e.getMessage());
            return false;
        } finally {
            if (channelSftp != null && channelSftp.isConnected()) {
                channelSftp.exit();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }

    // Move file TO SFTP server (upload)
    public static boolean moveToSftp(String server, int port, String user,
                                    String privateKeyPath, String passphrase,
                                    String localFilePath, String remoteFilePath) {
        Session session = null;
        ChannelSftp channelSftp = null;

        try {
            JSch jsch = new JSch();

            // Add private key (with optional passphrase)
            if (passphrase != null && !passphrase.isEmpty()) {
                jsch.addIdentity(privateKeyPath, passphrase);
            } else {
                jsch.addIdentity(privateKeyPath);
            }

            // Configure session
            session = jsch.getSession(user, server, port);
            session.setConfig("StrictHostKeyChecking", "no"); // For simplicity
            session.connect();

            // Open SFTP channel
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();

            // Upload file
            channelSftp.put(localFilePath, remoteFilePath);
            return true;
        } catch (JSchException | SftpException e) {
            System.err.println("SFTP error: " + e.getMessage());
            return false;
        } finally {
            if (channelSftp != null && channelSftp.isConnected()) {
                channelSftp.exit();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }
}
