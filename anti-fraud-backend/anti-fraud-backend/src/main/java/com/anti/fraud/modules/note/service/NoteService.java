package com.anti.fraud.modules.note.service;

import com.anti.fraud.modules.note.entity.Note;
import java.util.List;

public interface NoteService {

    /**
     * 创建笔记
     * @param note 笔记信息
     * @return 是否成功
     */
    boolean createNote(Note note);

    /**
     * 更新笔记
     * @param note 笔记信息
     * @return 是否成功
     */
    boolean updateNote(Note note);

    /**
     * 删除笔记
     * @param id 笔记ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteNote(Long id, Long userId);

    /**
     * 获取笔记详情
     * @param id 笔记ID
     * @return 笔记详情
     */
    Note getNoteById(Long id);

    /**
     * 获取用户的笔记列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 笔记列表
     */
    List<Note> getUserNotes(Long userId, int page, int size);

    /**
     * 获取公开笔记列表
     * @param page 页码
     * @param size 每页大小
     * @return 公开笔记列表
     */
    List<Note> getPublicNotes(int page, int size);

    /**
     * 点赞笔记
     * @param id 笔记ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean likeNote(Long id, Long userId);

    /**
     * 取消点赞
     * @param id 笔记ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean unlikeNote(Long id, Long userId);

    /**
     * 标记笔记为公开
     * @param id 笔记ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean markAsPublic(Long id, Long userId);

    /**
     * 标记笔记为私有
     * @param id 笔记ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean markAsPrivate(Long id, Long userId);
}
