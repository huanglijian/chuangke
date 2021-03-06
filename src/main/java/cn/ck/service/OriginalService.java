package cn.ck.service;

import cn.ck.entity.Original;

import cn.ck.entity.bean.OriColUser;


import cn.ck.entity.bean.OriUser;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ${author}
 * @since 2018-09-21
 */
public interface OriginalService extends IService<Original> {


    List<OriColUser> selectDesc(String id);
    /**
     * 主页取出最高分的原创
     * @return
     */
    List<Original> selectHeigestGradeOrigin();

    List<OriUser> selectAllOri(String type,String tag,int id);

    OriUser selectOriUser(int id);

    List<OriUser> selectOther(int id,String tag);

    List<OriUser> selectSearch(String key);
}
