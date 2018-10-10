package cn.ck.service;

import cn.ck.entity.Gradeori;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ${author}
 * @since 2018-09-21
 */
public interface GradeoriService extends IService<Gradeori> {

    boolean isScored(String userId, Integer origId) throws Exception;
}
