package cn.ck.controller.studio;


import cn.ck.entity.Jobuser;
import cn.ck.entity.Users;
import cn.ck.service.JobuserService;
import cn.ck.service.UsersService;
import cn.ck.utils.ResponseBo;
import cn.hutool.db.DaoTemplate;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

/**
 * @author 黄栎健
 */
@Controller
@RequestMapping("/member")
public class menberController {
    @Autowired
    private JobuserService jobuserService;
    @Autowired
    private UsersService usersService;

//    @GetMapping("/change")
//    public String  changemessage(String memberName,String jobName,String jobMoney,int juid){
//        System.out.println("----nemberName "+memberName+" ---- "+jobName+" ---- "+jobMoney);
//        System.out.println("---- "+juid);
//        Jobuser jobuser = new Jobuser();
//        jobuser = jobuserService.selectById(juid);
//        String userid = jobuser.getJuUsers();
//        int jobid = jobuser.getJuJobs();
//        return "redirect:/studioPage/memberInfo";
//    }

    //解雇工作室成员
    @GetMapping("/delete/{juid}")
    public String memberdel(@PathVariable("juid")int juid){
        System.out.println("---------juid "+juid);
//        添加事务，用户的办公室id先置空，在删除jobuser数据
        Jobuser jobuser = new Jobuser();
        jobuser = jobuserService.selectById(juid);
        String userid = jobuser.getJuUsers();
        Users users = new Users();
        users = usersService.selectById(userid);
        users.setUserStudio(null);
//        users.setUserEntrytime(null);
        users.setUserEntrytime(null);
        users.setUserQuittme(null);
        System.out.println("----users "+users);
        boolean b = usersService.updateAllColumnById(users);
//        boolean b = usersService.update(users,new EntityWrapper<Users>().eq("user_id",userid));
        System.out.println("---- "+b);
        boolean a = jobuserService.deleteById(juid);
        System.out.println("-----a "+a);
        return "redirect:/studioPage/memberInfo";
    }

    @GetMapping("/agree/{uid}")
    public String agreequit(@PathVariable("uid")String uid){
        System.out.println("uid "+uid);
//        获取users对象--工作室id，进入离开时间置为null
        Users users = usersService.selectById(uid);

        System.out.println("users "+users);
        users.setUserStudio(null);
        users.setUserEntrytime(null);
        users.setUserQuittme(null);

        usersService.updateAllColumnById(users);    //更新users表
        /*boolean a= usersService.update(users,new EntityWrapper<Users>().eq("user_id",uid));*/
//        System.out.println("a "+a);
        boolean a= jobuserService.delete(new EntityWrapper<Jobuser>().eq("ju_users",uid));
        System.out.println("a-"+a);
        return "redirect:/studioPage/memberReview";
    }

    @GetMapping("/noagree/{uid}")
    public String noagreequit(@PathVariable("uid")String uid){
        Users users = usersService.selectById(uid);
        users.setUserQuittme(null);
        boolean a= usersService.updateAllColumnById(users);    //更新users表
        System.out.println("a-"+a);
        return "redirect:/studioPage/memberReview";
    }
}
