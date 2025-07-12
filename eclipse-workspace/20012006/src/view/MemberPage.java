package view;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class MemberPage extends JFrame {
    private Connection conn;
    private Map<String, JButton> seatButtons = new HashMap<>();
    private ArrayList<String> selectedSeats = new ArrayList<>();

    public MemberPage(Connection conn) {
        this.conn = conn;
        setTitle("회원 페이지");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel searchMoviesPanel = new JPanel(new BorderLayout());
        searchMoviesPanel.add(createSearchMoviesPanel(), BorderLayout.CENTER);
        tabbedPane.addTab("모든 영화 조회", searchMoviesPanel);

        JPanel bookMoviePanel = new JPanel(new BorderLayout());
        bookMoviePanel.add(createBookMoviePanel(), BorderLayout.CENTER);
        tabbedPane.addTab("영화 예매", bookMoviePanel);

        JPanel viewBookingsPanel = new JPanel(new BorderLayout());
        viewBookingsPanel.add(createViewBookingsPanel(), BorderLayout.CENTER);
        tabbedPane.addTab("예매한 영화 조회", viewBookingsPanel);

        add(tabbedPane);
    }
    
    // 모든 영화 조회 탭
    private JPanel createSearchMoviesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));

        JLabel movieNameLabel = new JLabel("영화명:");
        JTextField movieNameField = new JTextField(20);
        JLabel directorLabel = new JLabel("감독명:");
        JTextField directorField = new JTextField(20);
        JLabel actorLabel = new JLabel("배우명:");
        JTextField actorField = new JTextField(20);
        JLabel genreLabel = new JLabel("장르:");
        JTextField genreField = new JTextField(20);

        JButton searchButton = new JButton("조회");

        searchPanel.add(movieNameLabel);
        searchPanel.add(movieNameField);
        searchPanel.add(directorLabel);
        searchPanel.add(directorField);
        searchPanel.add(actorLabel);
        searchPanel.add(actorField);
        searchPanel.add(genreLabel);
        searchPanel.add(genreField);
        searchPanel.add(searchButton);

        JTable moviesTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(moviesTable);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String movieName = movieNameField.getText().trim();
                String director = directorField.getText().trim();
                String actor = actorField.getText().trim();
                String genre = genreField.getText().trim();

                String query = "SELECT * FROM movies WHERE 1=1";
                if (!movieName.isEmpty()) query += " AND movie_title LIKE '%" + movieName + "%'";
                if (!director.isEmpty()) query += " AND director LIKE '%" + director + "%'";
                if (!actor.isEmpty()) query += " AND actor LIKE '%" + actor + "%'";
                if (!genre.isEmpty()) query += " AND genre LIKE '%" + genre + "%'";

                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(query)) {
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int columns = rsmd.getColumnCount();
                    Vector<String> columnNames = new Vector<>();
                    for (int i = 1; i <= columns; i++) {
                        columnNames.add(rsmd.getColumnName(i));
                    }

                    Vector<Vector<Object>> data = new Vector<>();
                    while (rs.next()) {
                        Vector<Object> vector = new Vector<>();
                        for (int i = 1; i <= columns; i++) {
                            vector.add(rs.getObject(i));
                        }
                        data.add(vector);
                    }
                    moviesTable.setModel(new DefaultTableModel(data, columnNames));
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

 // 영화 예매 메인 화면
    private JPanel createBookMoviePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        JLabel memberIdLabel = new JLabel("회원 ID:");
        JTextField memberIdField = new JTextField("kim0226", 20);
        leftPanel.add(memberIdLabel);
        leftPanel.add(memberIdField);

        JLabel movieLabel = new JLabel("영화 선택:");
        JComboBox<String> movieComboBox = new JComboBox<>();
        leftPanel.add(movieLabel);
        leftPanel.add(movieComboBox);

        JLabel scheduleLabel = new JLabel("상영 일정 선택:");
        JComboBox<String> scheduleComboBox = new JComboBox<>();
        leftPanel.add(scheduleLabel);
        leftPanel.add(scheduleComboBox);

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT movie_title FROM movies")) {
            while (rs.next()) {
                movieComboBox.addItem(rs.getString("movie_title"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // 영화 옵션
        movieComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scheduleComboBox.removeAllItems();
                String selectedMovie = (String) movieComboBox.getSelectedItem();
                if (selectedMovie != null) {
                    try (PreparedStatement stmt = conn.prepareStatement(
                            "SELECT s.schedule_number, s.theater_number, s.screening_start_time, s.screening_day " +
                                    "FROM schedules s JOIN movies m ON s.movie_number = m.movie_number " +
                                    "WHERE m.movie_title = ?")) {
                        stmt.setString(1, selectedMovie);
                        try (ResultSet rs = stmt.executeQuery()) {
                            while (rs.next()) {
                                String scheduleInfo = rs.getString("schedule_number") + " : " + rs.getString("theater_number") + " 관 - " + rs.getString("screening_day") + " " + rs.getString("screening_start_time");
                                scheduleComboBox.addItem(scheduleInfo);
                            }
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        JLabel personCountLabel = new JLabel("인원 선택:");
        JComboBox<Integer> personCountComboBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        leftPanel.add(personCountLabel);
        leftPanel.add(personCountComboBox);

        JLabel priceLabel = new JLabel("가격: 12,000 원");
        leftPanel.add(priceLabel);

        // 인원 옵션
        personCountComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedPersonCount = (int) personCountComboBox.getSelectedItem();
                priceLabel.setText("가격: " + (selectedPersonCount * 12000) + " 원");
            }
        });

        JLabel paymentMethodLabel = new JLabel("결제 방법:");
        JComboBox<String> paymentMethodComboBox = new JComboBox<>(new String[]{"신용카드", "계좌이체"});
        leftPanel.add(paymentMethodLabel);
        leftPanel.add(paymentMethodComboBox);

        JPanel buttonPanel = new JPanel();
        leftPanel.add(buttonPanel);

        JButton bookButton = new JButton("예매하기");
        buttonPanel.add(bookButton);

        JButton cancelButton = new JButton("예매 취소");
        buttonPanel.add(cancelButton);

        JLabel reservationNumLabel = new JLabel("예약 번호:");
        JTextField reservationNumField = new JTextField(20);
        leftPanel.add(reservationNumLabel);
        leftPanel.add(reservationNumField);

        JLabel finalDetailsLabel = new JLabel("최종 예매 내역:");
        JTextArea finalDetailsArea = new JTextArea(10, 30);
        finalDetailsArea.setEditable(false);
        leftPanel.add(finalDetailsLabel);
        leftPanel.add(new JScrollPane(finalDetailsArea));

        panel.add(leftPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new BorderLayout());

        JPanel seatsPanel = new JPanel();
        rightPanel.add(new JScrollPane(seatsPanel), BorderLayout.CENTER);

        JLabel screenLabel = new JLabel("SCREEN", SwingConstants.CENTER);
        screenLabel.setFont(new Font("Serif", Font.BOLD, 16));
        rightPanel.add(screenLabel, BorderLayout.NORTH);

        // 상영일정 옵션
        scheduleComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                seatsPanel.removeAll();
                seatsPanel.revalidate();
                seatsPanel.repaint();
                seatButtons.clear();
                selectedSeats.clear();

                String selectedSchedule = (String) scheduleComboBox.getSelectedItem();
                if (selectedSchedule != null) {
                    String scheduleNumber = selectedSchedule.split(" ")[0];
                    String theaterNumber = selectedSchedule.split(" ")[2];

                    try (PreparedStatement stmt = conn.prepareStatement("SELECT seat_number, seat_status FROM seat WHERE theater_number = ? AND schedule_number = ?")) {
                        stmt.setString(1, theaterNumber);
                        stmt.setString(2, scheduleNumber);
                        try (ResultSet rs = stmt.executeQuery()) {
                            seatsPanel.setLayout(new GridBagLayout());
                            GridBagConstraints gbc = new GridBagConstraints();
                            gbc.insets = new Insets(2, 2, 2, 2);
                            int rowIndex = 0;
                            int colIndex = 0;
                            int maxColumns = 10;

                            while (rs.next()) {
                                String seatNumber = rs.getString("seat_number");
                                String seatStatus = rs.getString("seat_status");
                                JButton seatButton = new JButton(seatNumber);
                                if (seatStatus.equals("y")) {
                                    seatButton.setEnabled(false);
                                    seatButton.setBackground(Color.RED);
                                } else {
                                    seatButton.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            String seatNumber = seatButton.getText();
                                            if (selectedSeats.contains(seatNumber)) {
                                                selectedSeats.remove(seatNumber);
                                                seatButton.setBackground(null);
                                            } else {
                                                selectedSeats.add(seatNumber);
                                                seatButton.setBackground(Color.GREEN);
                                            }
                                        }
                                    });
                                }
                                seatButtons.put(seatButton.getText(), seatButton);
                                gbc.gridx = colIndex;
                                gbc.gridy = rowIndex;
                                seatsPanel.add(seatButton, gbc);
                                colIndex++;
                                if (colIndex >= maxColumns) {
                                    colIndex = 0;
                                    rowIndex++;
                                }
                            }

                            if (seatsPanel.getComponentCount() >= 180) {
                                maxColumns = 15;
                            }

                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }

                    seatsPanel.revalidate();
                    seatsPanel.repaint();
                }
            }
        });

        panel.add(rightPanel, BorderLayout.CENTER);

        // 예매하기 버튼 기능
        bookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String memberId = memberIdField.getText().trim();
                String selectedMovie = (String) movieComboBox.getSelectedItem();
                String selectedSchedule = (String) scheduleComboBox.getSelectedItem();
                int personCount = (int) personCountComboBox.getSelectedItem();
                String paymentMethod = (String) paymentMethodComboBox.getSelectedItem();

                if (memberId.isEmpty() || selectedMovie == null || selectedSchedule == null || personCount == 0 || selectedSeats.size() != personCount || paymentMethod == null) {
                    JOptionPane.showMessageDialog(panel, "모든 필드를 입력하고, 인원수만큼 좌석을 선택하고, 결제 방법을 선택해주세요.", "에러", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String theaterNumber = selectedSchedule.split(" ")[2];
                String scheduleNumber = selectedSchedule.split(" ")[0];

                LocalDate reservationDate = LocalDate.now();
                String reservationNumber = memberId + "-" + reservationDate.toString();

                try (PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO reservation (member_id, payment_method, payment_status, payment_amount, payment_date) VALUES (?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, memberId);
                    stmt.setString(2, paymentMethod);
                    stmt.setString(3, "paid");
                    stmt.setInt(4, personCount * 12000);
                    stmt.setDate(5, Date.valueOf(reservationDate));
                    stmt.executeUpdate();

                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            reservationNumber = rs.getString(1);
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(panel, "예약 생성에 실패했습니다.", "에러", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                for (String seatNumber : selectedSeats) {
                    try (PreparedStatement stmt = conn.prepareStatement(
                            "INSERT INTO ticket (schedule_number, seat_number, theater_number, reservation_number, ticket_status, standard_price, sale_price) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                        stmt.setString(1, scheduleNumber);
                        stmt.setString(2, seatNumber);
                        stmt.setString(3, theaterNumber);
                        stmt.setString(4, reservationNumber);
                        stmt.setString(5, "Booked");
                        stmt.setInt(6, 12000);
                        stmt.setInt(7, 12000);
                        stmt.executeUpdate();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(panel, "티켓 생성 중 오류가 발생했습니다.", "에러", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                try (PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE seat SET seat_status = 'y' WHERE seat_number = ? AND theater_number = ? AND schedule_number = ?")) {
                    for (String seatNumber : selectedSeats) {
                        stmt.setString(1, seatNumber);
                        stmt.setString(2, theaterNumber);
                        stmt.setString(3, scheduleNumber);
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(panel, "좌석 상태 업데이트에 실패했습니다.", "에러", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                reservationNumField.setText(reservationNumber);
                finalDetailsArea.setText("예약 번호: " + reservationNumber + "\n" +
                        "회원 ID: " + memberId + "\n" +
                        "영화: " + selectedMovie + "\n" +
                        "상영 일정: " + selectedSchedule + "\n" +
                        "좌석: " + String.join(", ", selectedSeats) + "\n" +
                        "결제 방법: " + paymentMethod);

                JOptionPane.showMessageDialog(panel, "예약이 완료되었습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
                
                // 필드 초기화 - 디폴트 값 설정
                memberIdField.setText("kim0226");
                movieComboBox.setSelectedIndex(0); 
                scheduleComboBox.removeAllItems();
                personCountComboBox.setSelectedIndex(0); 
                priceLabel.setText("가격: 12,000 원");
                paymentMethodComboBox.setSelectedIndex(0);
                reservationNumField.setText("");
                finalDetailsArea.setText("");
                seatsPanel.removeAll();
                seatsPanel.revalidate();
                seatsPanel.repaint();
                seatButtons.clear();
                selectedSeats.clear();

                // 트리거 영화 선택 이벤트
                movieComboBox.dispatchEvent(new ActionEvent(movieComboBox, ActionEvent.ACTION_PERFORMED, null));
            }
        });

        // 예매 취소 버튼 기능
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 필드 초기화 - 디폴트 값 설정
                memberIdField.setText("kim0226");
                movieComboBox.setSelectedIndex(0);
                scheduleComboBox.removeAllItems();
                personCountComboBox.setSelectedIndex(0); 
                priceLabel.setText("가격: 12,000 원");
                paymentMethodComboBox.setSelectedIndex(0);
                reservationNumField.setText("");
                finalDetailsArea.setText("");
                seatsPanel.removeAll();
                seatsPanel.revalidate();
                seatsPanel.repaint();
                seatButtons.clear();
                selectedSeats.clear();

                // 트리거 영화 선택 이벤트
                movieComboBox.dispatchEvent(new ActionEvent(movieComboBox, ActionEvent.ACTION_PERFORMED, null));
            }
        });

        return panel;
    }



    // 예매한 영화 조회 메인 화면
    private JPanel createViewBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 회원 아이디 입력 패널
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));

        JLabel memberIdLabel = new JLabel("회원 ID:");
        JTextField memberIdField = new JTextField("kim0226", 20);
        JButton searchButton = new JButton("예매 조회");
        searchPanel.add(memberIdLabel);
        searchPanel.add(memberIdField);
        searchPanel.add(searchButton);

        // 예매 정보 테이블
        JTable bookingsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String memberId = memberIdField.getText().trim();
                if (memberId.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "회원 ID를 입력해주세요.", "에러", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String query = "SELECT r.reservation_number, m.movie_title, s.screening_day, s.theater_number, t.seat_number, t.sale_price " +
                        "FROM reservation r " +
                        "JOIN ticket t ON r.reservation_number = t.reservation_number " +
                        "JOIN schedules s ON t.schedule_number = s.schedule_number " +
                        "JOIN movies m ON s.movie_number = m.movie_number " +
                        "WHERE r.member_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, memberId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        ResultSetMetaData rsmd = rs.getMetaData();
                        int columns = rsmd.getColumnCount();
                        Vector<String> columnNames = new Vector<>();
                        for (int i = 1; i <= columns; i++) {
                            columnNames.add(rsmd.getColumnName(i));
                        }

                        Vector<Vector<Object>> data = new Vector<>();
                        while (rs.next()) {
                            Vector<Object> vector = new Vector<>();
                            for (int i = 1; i <= columns; i++) {
                                vector.add(rs.getObject(i));
                            }
                            data.add(vector);
                        }
                        bookingsTable.setModel(new DefaultTableModel(data, columnNames));
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(panel, "예매 조회에 실패했습니다.", "에러", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 상세 예매 정보 패널
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        JLabel detailsLabel = new JLabel("상세 예매 정보:");
        JTextArea detailsArea = new JTextArea(10, 30);
        detailsArea.setEditable(false);
        detailsPanel.add(detailsLabel);
        detailsPanel.add(new JScrollPane(detailsArea));
        panel.add(detailsPanel, BorderLayout.SOUTH);

        bookingsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && bookingsTable.getSelectedRow() != -1) {
                    int selectedRow = bookingsTable.getSelectedRow();
                    String reservationNumber = bookingsTable.getValueAt(selectedRow, 0).toString();
                    String query = "SELECT t.ticket_number, m.movie_title, s.screening_day, s.screening_start_time, s.theater_number, t.seat_number, t.sale_price " +
                            "FROM ticket t " +
                            "JOIN schedules s ON t.schedule_number = s.schedule_number " +
                            "JOIN movies m ON s.movie_number = m.movie_number " +
                            "WHERE t.reservation_number = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(query)) {
                        stmt.setString(1, reservationNumber);
                        try (ResultSet rs = stmt.executeQuery()) {
                            StringBuilder details = new StringBuilder();
                            while (rs.next()) {
                                details.append("티켓 번호: ").append(rs.getString("ticket_number")).append("\n")
                                		.append("영화: ").append(rs.getString("movie_title")).append("\n")
                                        .append("상영일: ").append(rs.getString("screening_day")).append("\n")
                                        .append("상영 시간: ").append(rs.getString("screening_start_time")).append("\n")
                                        .append("상영관 번호: ").append(rs.getString("theater_number")).append("\n")
                                        .append("좌석 번호: ").append(rs.getString("seat_number")).append("\n")
                                        .append("판매 가격: ").append(rs.getString("sale_price")).append(" 원\n\n");
                            }
                            detailsArea.setText(details.toString());
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(panel, "상세 예매 정보 조회에 실패했습니다.", "에러", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // 예매 정보 수정 및 삭제 버튼
        JButton deleteButton = new JButton("예매 삭제");
        JButton updateScheduleButton = new JButton("영화 / 상영일정 변경");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(deleteButton);
        buttonPanel.add(updateScheduleButton);
        detailsPanel.add(buttonPanel);

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = bookingsTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(panel, "삭제할 예매를 선택해주세요.", "에러", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String reservationNumber = bookingsTable.getValueAt(selectedRow, 0).toString();
                
                // 현재 예약에 대한 좌석 정보 조회
                List<String> seatNumbers = new ArrayList<>();
                String scheduleNumber = null;
                String theaterNumber = null;
                try (PreparedStatement stmt = conn.prepareStatement(
                        "SELECT t.seat_number, t.schedule_number, t.theater_number " +
                        "FROM ticket t " +
                        "WHERE t.reservation_number = ?")) {
                    stmt.setString(1, reservationNumber);
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            seatNumbers.add(rs.getString("seat_number"));
                            if (scheduleNumber == null) {
                                scheduleNumber = rs.getString("schedule_number");
                            }
                            if (theaterNumber == null) {
                                theaterNumber = rs.getString("theater_number");
                            }
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(panel, "좌석 정보를 불러오는 데 실패했습니다.", "에러", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    conn.setAutoCommit(false);

                    // 예매 정보 삭제
                    try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM reservation WHERE reservation_number = ?")) {
                        stmt.setString(1, reservationNumber);
                        int rowsDeleted = stmt.executeUpdate();
                        if (rowsDeleted == 0) {
                            conn.rollback();
                            JOptionPane.showMessageDialog(panel, "예매 삭제에 실패했습니다.", "에러", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }

                    // 티켓 정보 삭제
                    try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM ticket WHERE reservation_number = ?")) {
                        stmt.setString(1, reservationNumber);
                        stmt.executeUpdate();
                    }

                    // 좌석 상태 업데이트
                    try (PreparedStatement stmt = conn.prepareStatement(
                            "UPDATE seat SET seat_status = 'n' " +
                            "WHERE seat_number = ? AND schedule_number = ? AND theater_number = ?")) {
                        for (String seatNumber : seatNumbers) {
                            stmt.setString(1, seatNumber);
                            stmt.setString(2, scheduleNumber);
                            stmt.setString(3, theaterNumber);
                            stmt.addBatch();
                        }
                        stmt.executeBatch();
                    }

                    conn.commit();
                    JOptionPane.showMessageDialog(panel, "예매가 삭제되었습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
                    ((DefaultTableModel) bookingsTable.getModel()).removeRow(selectedRow);
                    detailsArea.setText("");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    try {
                        conn.rollback();
                    } catch (SQLException rollbackEx) {
                        rollbackEx.printStackTrace();
                    }
                    JOptionPane.showMessageDialog(panel, "예매 삭제 중 오류가 발생했습니다.", "에러", JOptionPane.ERROR_MESSAGE);
                } finally {
                    try {
                        conn.setAutoCommit(true);
                    } catch (SQLException autoCommitEx) {
                        autoCommitEx.printStackTrace();
                    }
                }
            }
        });

        updateScheduleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = bookingsTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(panel, "변경할 예매를 선택해주세요.", "에러", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String reservationNumber = bookingsTable.getValueAt(selectedRow, 0).toString();
                String movieTitle = bookingsTable.getValueAt(selectedRow, 1).toString();
                List<String> currentSeats = new ArrayList<>();

                // 티켓 개수와 현재 좌석 조회
                int ticketCount;
                try (PreparedStatement stmt = conn.prepareStatement("SELECT seat_number FROM ticket WHERE reservation_number = ?")) {
                    stmt.setString(1, reservationNumber);
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            currentSeats.add(rs.getString("seat_number"));
                        }
                        ticketCount = currentSeats.size();
                        if (ticketCount == 0) {
                            JOptionPane.showMessageDialog(panel, "티켓 정보를 불러오는 데 실패했습니다.", "에러", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(panel, "티켓 정보를 불러오는 데 실패했습니다.", "에러", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 상영 일정 및 좌석 선택 다이얼로그 생성
                JDialog scheduleDialog = new JDialog((Frame) null, "상영 일정 및 좌석 변경", true);
                scheduleDialog.setLayout(new BorderLayout());

                JPanel leftPanel = new JPanel();
                leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

                JLabel movieLabel = new JLabel("영화 선택:");
                JComboBox<String> movieComboBox = new JComboBox<>();
                leftPanel.add(movieLabel);
                leftPanel.add(movieComboBox);

                JLabel scheduleLabel = new JLabel("상영 일정 선택:");
                JComboBox<String> scheduleComboBox = new JComboBox<>();
                leftPanel.add(scheduleLabel);
                leftPanel.add(scheduleComboBox);

                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT movie_title FROM movies")) {
                    while (rs.next()) {
                        movieComboBox.addItem(rs.getString("movie_title"));
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                movieComboBox.setSelectedItem(movieTitle);

                movieComboBox.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        scheduleComboBox.removeAllItems();
                        String selectedMovie = (String) movieComboBox.getSelectedItem();
                        if (selectedMovie != null) {
                            try (PreparedStatement stmt = conn.prepareStatement(
                                    "SELECT s.schedule_number, s.theater_number, s.screening_start_time, s.screening_day " +
                                            "FROM schedules s JOIN movies m ON s.movie_number = m.movie_number " +
                                            "WHERE m.movie_title = ?")) {
                                stmt.setString(1, selectedMovie);
                                try (ResultSet rs = stmt.executeQuery()) {
                                    while (rs.next()) {
                                        String scheduleInfo = rs.getString("schedule_number") + " : " + rs.getString("theater_number") + " 관 - " + rs.getString("screening_day") + " " + rs.getString("screening_start_time");
                                        scheduleComboBox.addItem(scheduleInfo);
                                    }
                                }
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                });

                JPanel rightPanel = new JPanel(new BorderLayout());
                JPanel seatsPanel = new JPanel();
                rightPanel.add(new JScrollPane(seatsPanel), BorderLayout.CENTER);

                JLabel screenLabel = new JLabel("SCREEN", SwingConstants.CENTER);
                screenLabel.setFont(new Font("Serif", Font.BOLD, 16));
                rightPanel.add(screenLabel, BorderLayout.NORTH);

                scheduleComboBox.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        seatsPanel.removeAll();
                        seatsPanel.revalidate();
                        seatsPanel.repaint();
                        seatButtons.clear();
                        selectedSeats.clear();

                        String selectedSchedule = (String) scheduleComboBox.getSelectedItem();
                        if (selectedSchedule != null) {
                            String scheduleNumber = selectedSchedule.split(" ")[0];
                            String theaterNumber = selectedSchedule.split(" ")[2];

                            try (PreparedStatement stmt = conn.prepareStatement("SELECT seat_number, seat_status FROM seat WHERE theater_number = ? AND schedule_number = ?")) {
                                stmt.setString(1, theaterNumber);
                                stmt.setString(2, scheduleNumber);
                                try (ResultSet rs = stmt.executeQuery()) {
                                    seatsPanel.setLayout(new GridBagLayout());
                                    GridBagConstraints gbc = new GridBagConstraints();
                                    gbc.insets = new Insets(2, 2, 2, 2);
                                    int rowIndex = 0;
                                    int colIndex = 0;
                                    int maxColumns = 10;

                                    while (rs.next()) {
                                        String seatNumber = rs.getString("seat_number");
                                        String seatStatus = rs.getString("seat_status");
                                        JButton seatButton = new JButton(seatNumber);
                                        if (seatStatus.equals("y")) {
                                            seatButton.setEnabled(false);
                                            seatButton.setBackground(Color.RED);
                                        } else {
                                            seatButton.addActionListener(new ActionListener() {
                                                @Override
                                                public void actionPerformed(ActionEvent e) {
                                                    String seatNumber = seatButton.getText();
                                                    if (selectedSeats.contains(seatNumber)) {
                                                        selectedSeats.remove(seatNumber);
                                                        seatButton.setBackground(null);
                                                    } else {
                                                        selectedSeats.add(seatNumber);
                                                        seatButton.setBackground(Color.GREEN);
                                                    }
                                                }
                                            });
                                        }
                                        seatButtons.put(seatButton.getText(), seatButton);
                                        gbc.gridx = colIndex;
                                        gbc.gridy = rowIndex;
                                        seatsPanel.add(seatButton, gbc);
                                        colIndex++;
                                        if (colIndex >= maxColumns) {
                                            colIndex = 0;
                                            rowIndex++;
                                        }
                                    }

                                    if (seatsPanel.getComponentCount() >= 180) {
                                        maxColumns = 15;
                                    }

                                }
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }

                            seatsPanel.revalidate();
                            seatsPanel.repaint();
                        }
                    }
                });

                JPanel buttonPanel = new JPanel();
                JButton confirmButton = new JButton("예매 변경");
                JButton cancelButton = new JButton("변경 취소");

                confirmButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String selectedSchedule = (String) scheduleComboBox.getSelectedItem();
                        if (selectedSchedule == null) {
                            JOptionPane.showMessageDialog(scheduleDialog, "상영 일정을 선택해주세요.", "에러", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        String scheduleNumber = selectedSchedule.split(" ")[0];
                        String theaterNumber = selectedSchedule.split(" ")[2];

                        if (selectedSeats.size() != ticketCount) {
                            JOptionPane.showMessageDialog(scheduleDialog, "선택한 좌석 수가 티켓 수와 일치하지 않습니다.", "에러", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        // 예매 정보 업데이트
                        try {
                            conn.setAutoCommit(false);

                            // 기존 예매 정보 가져오기
                            String oldScheduleNumber = null;
                            String oldTheaterNumber = null;
                            try (PreparedStatement selectStmt = conn.prepareStatement(
                                    "SELECT schedule_number, theater_number FROM ticket WHERE reservation_number = ?")) {
                                selectStmt.setString(1, reservationNumber);
                                try (ResultSet rs = selectStmt.executeQuery()) {
                                    if (rs.next()) {
                                        oldScheduleNumber = rs.getString("schedule_number");
                                        oldTheaterNumber = rs.getString("theater_number");
                                    }
                                }
                            }

                            // 기존 좌석 상태 업데이트
                            if (oldScheduleNumber != null && oldTheaterNumber != null) {
                                try (PreparedStatement updateOldSeatsStmt = conn.prepareStatement(
                                        "UPDATE seat SET seat_status = 'n' WHERE seat_number = ? AND schedule_number = ? AND theater_number = ?")) {
                                    for (String seat : currentSeats) {
                                        updateOldSeatsStmt.setString(1, seat);
                                        updateOldSeatsStmt.setString(2, oldScheduleNumber);
                                        updateOldSeatsStmt.setString(3, oldTheaterNumber);
                                        updateOldSeatsStmt.addBatch();
                                    }
                                    updateOldSeatsStmt.executeBatch();
                                }
                            }

                            try (PreparedStatement updateStmt = conn.prepareStatement(
                                    "UPDATE ticket SET schedule_number = ?, theater_number = ?, seat_number = ? WHERE reservation_number = ? AND seat_number = ?")) {
                                for (int i = 0; i < selectedSeats.size(); i++) {
                                    updateStmt.setInt(1, Integer.parseInt(scheduleNumber));
                                    updateStmt.setString(2, theaterNumber);
                                    updateStmt.setString(3, selectedSeats.get(i));
                                    updateStmt.setString(4, reservationNumber);
                                    updateStmt.setString(5, currentSeats.get(i));
                                    updateStmt.addBatch();
                                }
                                updateStmt.executeBatch();
                            }

                            // 새로운 좌석 상태 업데이트
                            try (PreparedStatement updateNewSeatsStmt = conn.prepareStatement(
                                    "UPDATE seat SET seat_status = 'y' WHERE seat_number = ? AND schedule_number = ? AND theater_number = ?")) {
                                for (String seat : selectedSeats) {
                                    updateNewSeatsStmt.setString(1, seat);
                                    updateNewSeatsStmt.setString(2, scheduleNumber);
                                    updateNewSeatsStmt.setString(3, theaterNumber);
                                    updateNewSeatsStmt.addBatch();
                                }
                                updateNewSeatsStmt.executeBatch();
                            }

                            conn.commit();
                            JOptionPane.showMessageDialog(scheduleDialog, "예매 정보가 성공적으로 변경되었습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
                            scheduleDialog.dispose();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            try {
                                conn.rollback();
                            } catch (SQLException rollbackEx) {
                                rollbackEx.printStackTrace();
                            }
                            JOptionPane.showMessageDialog(scheduleDialog, "예매 정보 변경에 실패했습니다.", "에러", JOptionPane.ERROR_MESSAGE);
                        } finally {
                            try {
                                conn.setAutoCommit(true);
                            } catch (SQLException autoCommitEx) {
                                autoCommitEx.printStackTrace();
                            }
                        }
                    }
                });


                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        scheduleDialog.dispose();
                    }
                });

                buttonPanel.add(confirmButton);
                buttonPanel.add(cancelButton);

                scheduleDialog.add(leftPanel, BorderLayout.WEST);
                scheduleDialog.add(rightPanel, BorderLayout.CENTER);
                scheduleDialog.add(buttonPanel, BorderLayout.SOUTH);

                scheduleDialog.setSize(900, 600); 
                scheduleDialog.setLocationRelativeTo(panel);
                scheduleDialog.setVisible(true);
            }
        });

        return panel;
    }

}
