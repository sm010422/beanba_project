package likelion.beanBa.backendProject.chatting.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import likelion.beanBa.backendProject.chatting.dto.ChattingRoomListResponse;
import likelion.beanBa.backendProject.chatting.repository.ChattingRoomCustom;

import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 채팅룸 리스트 가져오기 위한 repository
 * */
@Repository
public class ChattingRoomCustomImpl implements ChattingRoomCustom {

    @PersistenceContext
    private EntityManager entityManager;

    /*
     * 모든 채팅룸 리스트 가져오기
     * */
    @Override
    public List<ChattingRoomListResponse> getAllChattingRoomList(Long memberPk) {

        String sql = "SELECT chat_list.chat_room_pk, chat_list.message, chat_list.message_at, chat_list.chat_with" +
                "            , m.nickname as chat_with_nickname, read_yn, chat_list.post_pk, chat_list.title " +
                "  FROM (SELECT * " +
                "          FROM (SELECT cr.chat_room_pk, cr.post_pk, cm.message, cm.message_at" +
                "                       , IF(cr.buy_member_pk = :memberPk, sp.member_pk, cr.buy_member_pk) AS chat_with " +
                "                       , IF(cm.read_yn !='Y' AND cm.member_pk_from != :memberPk, 'N', 'Y') AS read_yn, sp.title " +
                "                       , ROW_NUMBER() OVER (PARTITION BY cm.chat_room_pk ORDER BY cm.chat_message_pk DESC) AS rn " +
                "                  FROM chat_room cr LEFT OUTER JOIN chat_message cm ON cr.chat_room_pk = cm.chat_room_pk " +
                "                       LEFT OUTER JOIN sale_post sp on cr.post_pk = sp.post_pk" +
                "                 WHERE ( cr.buy_member_pk = :memberPk " +
                "                    OR sp.member_pk = :memberPk ) " +
                "                   AND cm.message_at IS NOT NULL " +
                "               ) AS ranked " +
                "         WHERE ranked.rn = 1" +
                "      ORDER BY ranked.message_at DESC) chat_list " +
                "      LEFT OUTER JOIN member m " +
                "      ON chat_list.chat_with = m.member_pk" +
                "      ORDER BY chat_list.message_at DESC ";

        List<Object[]> resultList = entityManager.createNativeQuery(sql)
                .setParameter("memberPk", memberPk)
                .getResultList();

        return resultList.stream()
                .map(row -> new ChattingRoomListResponse(
                        ((Number) row[0]).longValue(), // 채팅룸 pk
                        (String) row[1], // 채팅룸의 가장 최근 message
                        ((Timestamp) row[2]).toLocalDateTime(), // message 전송시간
                        ((Long) row[3]).longValue(), // 채팅룸 대화상대 pk
                        (String) row[4], // 채팅룸 대화상대 이름
                        String.valueOf(row[5]), // 내가 해당 메시지 읽었는 지 유무
                        ((Long) row[6]).longValue(), // 상품 pk
                        memberPk, // 내 계정 pk값
                        String.valueOf(row[7]) // 상품 제목
                ))
                .collect(Collectors.toList());
    }

    /*
     * 특정 상품에 대한 채팅룸 리스트 가져오기 in 상품상세화면
     */
    @Override
    public List<ChattingRoomListResponse> getChattingRoomListByPostPk(Long postPk, Long memberPk) {

        String sql = "SELECT chat_list.chat_room_pk, chat_list.message, chat_list.message_at, chat_list.chat_with" +
                "            , m.nickname as chat_with_nickname, read_yn, chat_list.post_pk, chat_list.title " +
                "  FROM (SELECT * " +
                "          FROM (SELECT cr.chat_room_pk, cr.post_pk, cm.message, cm.message_at" +
                "                       , cr.buy_member_pk AS chat_with " +
                "                       , IF(cm.read_yn !='Y' AND cm.member_pk_from != :memberPk, 'N', 'Y') AS read_yn, sp.title " +
                "                       , ROW_NUMBER() OVER (PARTITION BY cm.chat_room_pk ORDER BY cm.chat_message_pk DESC) AS rn " +
                "                  FROM chat_room cr LEFT OUTER JOIN chat_message cm ON cr.chat_room_pk = cm.chat_room_pk " +
                "                       LEFT OUTER JOIN sale_post sp on cr.post_pk = sp.post_pk" +
                "                 WHERE cr.post_pk = :postPk " +
                "                   AND cm.message_at IS NOT NULL " +
                "               ) AS ranked " +
                "         WHERE ranked.rn = 1" +
                "      ORDER BY ranked.message_at DESC) chat_list " +
                "      LEFT OUTER JOIN member m " +
                "      ON chat_list.chat_with = m.member_pk" +
                "      ORDER BY chat_list.message_at DESC ";

        List<Object[]> resultList = entityManager.createNativeQuery(sql)
                .setParameter("postPk", postPk)
                .setParameter("memberPk", memberPk)
                .getResultList();

        return resultList.stream()
                .map(row -> new ChattingRoomListResponse(
                        ((Number) row[0]).longValue(), // 채팅룸 pk
                        (String) row[1], // 채팅룸의 가장 최근 message
                        ((Timestamp) row[2]).toLocalDateTime(), // message 전송시간
                        ((Long) row[3]).longValue(), // 채팅룸 대화상대 pk
                        (String) row[4], // 채팅룸 대화상대 이름
                        String.valueOf(row[5]), // 내가 해당 메시지 읽었는 지 유무
                        ((Long) row[6]).longValue(), // 상품 pk
                        memberPk ,// 내 계정 pk값,
                        String.valueOf(row[7]) // 상품 제목
                ))
                .collect(Collectors.toList());
    }

    /*
     * 모든 채팅룸 중 안 읽은 메시지가 있는 채팅룸 리스트 가져오기(로그인 후 호출 됨)
     * */
    @Override
    public List<ChattingRoomListResponse> getUnreadChattingRoomList(Long memberPk) {

        String sql = "SELECT chat_list.chat_room_pk, chat_list.message, chat_list.message_at, chat_list.chat_with" +
                "            , m.nickname as chat_with_nickname, read_yn, chat_list.post_pk, chat_list.title " +
                "  FROM (SELECT * " +
                "          FROM (SELECT cr.chat_room_pk, cr.post_pk, cm.message, cm.message_at" +
                "                       , IF(cr.buy_member_pk = :memberPk, sp.member_pk, cr.buy_member_pk) AS chat_with " +
                "                       , IF(cm.read_yn !='Y' AND cm.member_pk_from != :memberPk, 'N', 'Y') AS read_yn, sp.title " +
                "                       , ROW_NUMBER() OVER (PARTITION BY cm.chat_room_pk ORDER BY cm.chat_message_pk DESC) AS rn " +
                "                  FROM chat_room cr LEFT OUTER JOIN chat_message cm ON cr.chat_room_pk = cm.chat_room_pk " +
                "                       LEFT OUTER JOIN sale_post sp on cr.post_pk = sp.post_pk" +
                "                 WHERE ( cr.buy_member_pk = :memberPk " +
                "                    OR sp.member_pk = :memberPk ) " +
                "                   AND cm.message_at IS NOT NULL " +
                "               ) AS ranked " +
                "         WHERE ranked.rn = 1" +
                "      ORDER BY ranked.message_at DESC) chat_list " +
                "      LEFT OUTER JOIN member m " +
                "      ON chat_list.chat_with = m.member_pk "+
                "   WHERE chat_list.read_yn = 'N' " +
                "      ORDER BY chat_list.message_at DESC ";

        List<Object[]> resultList = entityManager.createNativeQuery(sql)
                .setParameter("memberPk", memberPk)
                .getResultList();

        return resultList.stream()
                .map(row -> new ChattingRoomListResponse(
                        ((Number) row[0]).longValue(), // 채팅룸 pk
                        (String) row[1], // 채팅룸의 가장 최근 message
                        ((Timestamp) row[2]).toLocalDateTime(), // message 전송시간
                        ((Long) row[3]).longValue(), // 채팅룸 대화상대 pk
                        (String) row[4], // 채팅룸 대화상대 이름
                        String.valueOf(row[5]), // 내가 해당 메시지 읽었는 지 유무
                        ((Long) row[6]).longValue(), // 상품 pk
                        memberPk, // 내 계정 pk값
                        String.valueOf(row[7]) // 상품 제목
                ))
                .collect(Collectors.toList());
    }

}
