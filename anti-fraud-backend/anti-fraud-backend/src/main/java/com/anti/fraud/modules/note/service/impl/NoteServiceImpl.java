package com.anti.fraud.modules.note.service.impl;

import com.anti.fraud.modules.note.entity.Note;
import com.anti.fraud.modules.note.mapper.NoteMapper;
import com.anti.fraud.modules.note.service.NoteService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteMapper noteMapper;

    @Override
    public boolean createNote(Note note) {
        try {
            note.setLikeCount(0);
            note.setCommentCount(0);
            note.setStatus(1);
            noteMapper.insert(note);
            log.info("创建笔记成功: id={}, userId={}", note.getId(), note.getUserId());
            return true;
        } catch (Exception e) {
            log.error("创建笔记失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean updateNote(Note note) {
        try {
            noteMapper.updateById(note);
            log.info("更新笔记成功: id={}, userId={}", note.getId(), note.getUserId());
            return true;
        } catch (Exception e) {
            log.error("更新笔记失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean deleteNote(Long id, Long userId) {
        try {
            Note note = noteMapper.selectById(id);
            if (note != null && note.getUserId().equals(userId)) {
                noteMapper.deleteById(id);
                log.info("删除笔记成功: id={}, userId={}", id, userId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("删除笔记失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Note getNoteById(Long id) {
        try {
            return noteMapper.selectById(id);
        } catch (Exception e) {
            log.error("获取笔记详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<Note> getUserNotes(Long userId, int page, int size) {
        try {
            LambdaQueryWrapper<Note> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Note::getUserId, userId)
                    .orderByDesc(Note::getUpdateTime);

            IPage<Note> pageInfo = new Page<>(page, size);
            noteMapper.selectPage(pageInfo, queryWrapper);

            return pageInfo.getRecords();
        } catch (Exception e) {
            log.error("获取用户笔记列表失败: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Note> getPublicNotes(int page, int size) {
        try {
            LambdaQueryWrapper<Note> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Note::getIsPublic, 1)
                    .eq(Note::getStatus, 1)
                    .orderByDesc(Note::getLikeCount)
                    .orderByDesc(Note::getCreateTime);

            IPage<Note> pageInfo = new Page<>(page, size);
            noteMapper.selectPage(pageInfo, queryWrapper);

            return pageInfo.getRecords();
        } catch (Exception e) {
            log.error("获取公开笔记列表失败: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public boolean likeNote(Long id, Long userId) {
        try {
            Note note = noteMapper.selectById(id);
            if (note != null) {
                note.setLikeCount(note.getLikeCount() + 1);
                noteMapper.updateById(note);
                log.info("点赞笔记成功: id={}, userId={}", id, userId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("点赞笔记失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean unlikeNote(Long id, Long userId) {
        try {
            Note note = noteMapper.selectById(id);
            if (note != null && note.getLikeCount() > 0) {
                note.setLikeCount(note.getLikeCount() - 1);
                noteMapper.updateById(note);
                log.info("取消点赞成功: id={}, userId={}", id, userId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("取消点赞失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean markAsPublic(Long id, Long userId) {
        try {
            Note note = noteMapper.selectById(id);
            if (note != null && note.getUserId().equals(userId)) {
                note.setIsPublic(1);
                noteMapper.updateById(note);
                log.info("笔记标记为公开: id={}, userId={}", id, userId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("标记笔记为公开失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean markAsPrivate(Long id, Long userId) {
        try {
            Note note = noteMapper.selectById(id);
            if (note != null && note.getUserId().equals(userId)) {
                note.setIsPublic(0);
                noteMapper.updateById(note);
                log.info("笔记标记为私有: id={}, userId={}", id, userId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("标记笔记为私有失败: {}", e.getMessage(), e);
            return false;
        }
    }
}
