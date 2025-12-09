package chatting.appli.project;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Client implements ActionListener {

    JTextField text;
    static JPanel a1;
    static Box vertical = Box.createVerticalBox();
    static DataOutputStream dout;
    JFrame f;
    String userName = "Aadi 👤";

    Client() {
        f = new JFrame("Client Chat");
        f.setLayout(null);

        JPanel header = new JPanel();
        header.setBackground(new Color(7, 94, 84));
        header.setBounds(0, 0, 450, 70);
        header.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 20));

        JLabel name = new JLabel(userName);
        name.setForeground(Color.WHITE);
        name.setFont(new Font("SAN_SERIF", Font.BOLD, 18));
        header.add(name);
        f.add(header);

        a1 = new JPanel();
        a1.setLayout(new BoxLayout(a1, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(a1);
        scrollPane.setBounds(5, 75, 440, 570);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        f.add(scrollPane);

        text = new JTextField();
        text.setBounds(5, 655, 310, 40);
        text.setFont(new Font("SAN_SERIF", Font.PLAIN, 16));
        f.add(text);

        JButton send = new JButton("Send");
        send.setBounds(320, 655, 123, 40);
        send.setBackground(new Color(7, 94, 84));
        send.setForeground(Color.WHITE);
        send.addActionListener(this);
        f.add(send);

        f.setSize(450, 700);
        f.setLocation(700, 50);
        f.setUndecorated(true);
        f.getContentPane().setBackground(Color.WHITE);
        f.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        try {
            String out = text.getText().trim();
            if (out.isEmpty()) return;

            JPanel p2 = formatLabel(userName, out, true);
            JPanel right = new JPanel(new BorderLayout());
            right.add(p2, BorderLayout.LINE_END);

            vertical.add(right);
            vertical.add(Box.createVerticalStrut(15));

            a1.add(vertical, BorderLayout.PAGE_START);
            dout.writeUTF(out);
            text.setText("");
            a1.revalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JPanel formatLabel(String sender, String msg, boolean isSender) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel name = new JLabel(sender);
        name.setFont(new Font("SAN_SERIF", Font.BOLD, 13));
        name.setForeground(isSender ? new Color(0, 102, 51) : new Color(51, 51, 51));
        panel.add(name);

        JLabel output = new JLabel("<html><p style=\"width:150px\">" + msg + "</p></html>");
        output.setFont(new Font("Tahoma", Font.PLAIN, 16));
        output.setBackground(isSender ? new Color(220, 248, 198) : new Color(240, 240, 240));
        output.setOpaque(true);
        output.setBorder(new EmptyBorder(10, 15, 10, 15));

        panel.add(output);

        JLabel time = new JLabel(new SimpleDateFormat("hh:mm a").format(new Date()));
        time.setFont(new Font("SAN_SERIF", Font.PLAIN, 12));
        time.setForeground(Color.GRAY);
        panel.add(time);

        return panel;
    }

    public static void main(String[] args) {
        new Client();

        try (Socket s = new Socket("127.0.0.1", 7008)) {
            System.out.println("✅ Connected to server!");
            DataInputStream din = new DataInputStream(s.getInputStream());
            dout = new DataOutputStream(s.getOutputStream());

            while (true) {
                try {
                    String msg = din.readUTF();
                    if (msg.equalsIgnoreCase("exit")) break;

                    JPanel panel = formatLabel(" 💻", msg, false);
                    SwingUtilities.invokeLater(() -> {
                        JPanel left = new JPanel(new BorderLayout());
                        left.add(panel, BorderLayout.LINE_START);
                        vertical.add(left);
                        vertical.add(Box.createVerticalStrut(15));
                        a1.revalidate();
                    });
                } catch (EOFException e) {
                    System.out.println("⚠ Server disconnected.");
                    break;
                }
            }

            din.close();
            dout.close();
            s.close();
            System.out.println("💤 Client stopped.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
