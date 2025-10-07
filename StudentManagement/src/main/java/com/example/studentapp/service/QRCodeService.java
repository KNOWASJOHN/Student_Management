package com.example.studentapp.service;

import com.example.studentapp.model.Student;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Pure Java QR Code Service using ZXing library
 * Only for students with hostel accommodation
 */
public class QRCodeService {
    private static final int QR_CODE_SIZE = 300;

    /**
     * Generates QR code image for hostel students
     */
    public BufferedImage generateStudentQRCode(Student student) {
        if (!student.isHostel()) {
            return null;
        }

        try {
            String qrContent = formatStudentData(student);

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(
                    qrContent,
                    BarcodeFormat.QR_CODE,
                    QR_CODE_SIZE,
                    QR_CODE_SIZE
            );

            return MatrixToImageWriter.toBufferedImage(bitMatrix);

        } catch (WriterException e) {
            System.err.println("Failed to generate QR code: " + e.getMessage());
            return null;
        }
    }

    /**
     * Formats student data for QR code content
     */
    private String formatStudentData(Student student) {
        return String.format(
                "STUDENT INFORMATION\n" +
                        "===================\n" +
                        "ID: %s\n" +
                        "Name: %s\n" +
                        "Email: %s\n" +
                        "Branch: %s\n" +
                        "Semester: %s\n" +
                        "Phone: %s\n" +
                        "Hostel: Yes\n" +
                        "Role: %s\n" +
                        "===================\n" +
                        "Student Management System",
                student.getStudentId(),
                student.getName(),
                student.getEmail(),
                student.getBranch(),
                student.getSemester(),
                student.getPhone(),
                student.getRole() != null ? student.getRole().toString() : "STUDENT"
        );
    }

    /**
     * Displays QR code in a dialog
     */
    public void showQRCodeDialog(Student student, Component parent) {
        if (!student.isHostel()) {
            JOptionPane.showMessageDialog(parent,
                    "QR Code is only available for hostel students.",
                    "Not a Hostel Student",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        BufferedImage qrImage = generateStudentQRCode(student);

        if (qrImage == null) {
            JOptionPane.showMessageDialog(parent,
                    "Failed to generate QR code.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog qrDialog = new JDialog(
                parent instanceof Window ? (Window) parent : null,
                "Student QR Code - " + student.getName(),
                Dialog.ModalityType.APPLICATION_MODAL
        );

        qrDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        qrDialog.setSize(400, 550);
        qrDialog.setLocationRelativeTo(parent);
        qrDialog.setResizable(false);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("Student QR Code", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 100, 200));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // QR Code panel
        JPanel qrPanel = new JPanel(new BorderLayout());
        qrPanel.setBackground(Color.WHITE);
        qrPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        ImageIcon qrIcon = new ImageIcon(qrImage);
        JLabel qrLabel = new JLabel(qrIcon);
        qrLabel.setHorizontalAlignment(JLabel.CENTER);
        qrPanel.add(qrLabel, BorderLayout.CENTER);

        mainPanel.add(qrPanel, BorderLayout.CENTER);

        // Student info panel
        JPanel infoPanel = createStudentInfoPanel(student);
        mainPanel.add(infoPanel, BorderLayout.SOUTH);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Save QR Code");
        saveButton.addActionListener(e -> saveQRCode(qrImage, student, parent));

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> qrDialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        qrDialog.add(mainPanel);
        qrDialog.setVisible(true);
    }

    /**
     * Creates student information panel
     */
    private JPanel createStudentInfoPanel(Student student) {
        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Student Information"
        ));

        infoPanel.add(createInfoLabel("Name: " + student.getName()));
        infoPanel.add(createInfoLabel("ID: " + student.getStudentId()));
        infoPanel.add(createInfoLabel("Email: " + student.getEmail()));
        infoPanel.add(createInfoLabel("Branch: " + student.getBranch()));
        infoPanel.add(createInfoLabel("Semester: " + student.getSemester()));
        infoPanel.add(createInfoLabel("Phone: " + student.getPhone()));
        infoPanel.add(createInfoLabel("Hostel: Yes 🏠"));

        return infoPanel;
    }

    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        return label;
    }

    /**
     * Saves QR code to file
     */
    private void saveQRCode(BufferedImage qrImage, Student student, Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save QR Code");
        fileChooser.setSelectedFile(new File(student.getName() + "_qr.png"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "PNG Images", "png"));

        if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File outputFile = fileChooser.getSelectedFile();
            try {
                // Ensure .png extension
                if (!outputFile.getName().toLowerCase().endsWith(".png")) {
                    outputFile = new File(outputFile.getAbsolutePath() + ".png");
                }

                // Convert BufferedImage to file
                javax.imageio.ImageIO.write(qrImage, "PNG", outputFile);

                JOptionPane.showMessageDialog(parent,
                        "QR code saved successfully to:\n" + outputFile.getAbsolutePath(),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException e) {
                JOptionPane.showMessageDialog(parent,
                        "Failed to save QR code: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}