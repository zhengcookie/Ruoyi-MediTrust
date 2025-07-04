package com.ruoyi.system.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

//import com.baomidou.mybatisplus.core.conditions.Wrapper;
//import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.SysNotice;
import com.ruoyi.system.mapper.SysNoticeMapper;
import com.ruoyi.system.service.ISysNoticeService;

/**
 * 公告 服务层实现
 *
 * @author ruoyi
 */
@Service
public class SysNoticeServiceImpl implements ISysNoticeService
{
    @Autowired
    private SysNoticeMapper noticeMapper;

    /**
     * 查询公告信息
     *
     * @param noticeId 公告ID
     * @return 公告信息
     */
    @Override
    public SysNotice selectNoticeById(Long noticeId)
    {
        return noticeMapper.selectNoticeById(noticeId);
    }

    /**
     * 查询公告列表
     *
     * @param notice 公告信息
     * @return 公告集合
     */
    @Override
    public List<SysNotice> selectNoticeList(SysNotice notice)
    {
        return noticeMapper.selectNoticeList(notice);
    }

    /**
     * 新增公告
     *
     * @param notice 公告信息
     * @return 结果
     */
    @Override
    public int insertNotice(SysNotice notice)
    {
        return noticeMapper.insertNotice(notice);
    }

    /**
     * 修改公告
     *
     * @param notice 公告信息
     * @return 结果
     */
    @Override
    public int updateNotice(SysNotice notice)
    {
        return noticeMapper.updateNotice(notice);
    }

    /**
     * 删除公告对象
     *
     * @param noticeId 公告ID
     * @return 结果
     */
    @Override
    public int deleteNoticeById(Long noticeId)
    {
        return noticeMapper.deleteNoticeById(noticeId);
    }

    /**
     * 批量删除公告信息
     *
     * @param noticeIds 需要删除的公告ID
     * @return 结果
     */
    @Override
    public int deleteNoticeByIds(Long[] noticeIds)
    {
        return noticeMapper.deleteNoticeByIds(noticeIds);
    }

//    @Override
//    public boolean saveBatch(Collection<SysNotice> entityList, int batchSize) {
//        return false;
//    }
//
//    @Override
//    public boolean saveOrUpdateBatch(Collection<SysNotice> entityList, int batchSize) {
//        return false;
//    }
//
//    @Override
//    public boolean updateBatchById(Collection<SysNotice> entityList, int batchSize) {
//        return false;
//    }
//
//    @Override
//    public boolean saveOrUpdate(SysNotice entity) {
//        return false;
//    }
//
//    @Override
//    public SysNotice getOne(Wrapper<SysNotice> queryWrapper, boolean throwEx) {
//        return null;
//    }
//
//    @Override
//    public Map<String, Object> getMap(Wrapper<SysNotice> queryWrapper) {
//        return Map.of();
//    }
//
//    @Override
//    public <V> V getObj(Wrapper<SysNotice> queryWrapper, Function<? super Object, V> mapper) {
//        return null;
//    }
//
//    @Override
//    public BaseMapper<SysNotice> getBaseMapper() {
//        return null;
//    }
//
//    @Override
//    public Class<SysNotice> getEntityClass() {
//        return null;
//    }
}
