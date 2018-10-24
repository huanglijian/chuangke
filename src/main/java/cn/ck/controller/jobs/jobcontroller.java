package cn.ck.controller.jobs;

import cn.ck.controller.AbstractController;
import cn.ck.controller.FileController;
import cn.ck.entity.*;
import cn.ck.entity.bean.JobsStudio;
import cn.ck.entity.bean.resume;
import cn.ck.service.JobsService;
import cn.ck.service.JobuserService;
import cn.ck.service.StudioService;
import cn.ck.service.UsersService;
import cn.ck.utils.ConstCofig;
import cn.ck.utils.ResponseBo;
import cn.ck.utils.utils_hlj.fartime;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

@Controller
@RequestMapping("/ForJob")
//@ResponseBody
public class jobcontroller extends AbstractController {
    @Autowired
    private JobsService jobsService;
    @Autowired
    private StudioService studioService;
    @Autowired
    private JobuserService jobuserService;
    @Autowired
    private UsersService usersService;

    //    @RequestMapping("/search")
    @GetMapping("/search")
    public String search() {
        System.out.println("search-----");
        Jobs jobs = new Jobs();
//        获得所有的招聘信息
        List<Jobs> jobss = jobsService.selectList(new EntityWrapper<Jobs>());
//        获得招聘的个数
        int jNum = jobsService.selectCount(new EntityWrapper<Jobs>());
        return "jobs/search";
    }

    //        获得所有的招聘信息
    @PostMapping("/search/jobs/{pagenum}")
    @ResponseBody
    public ResponseBo searchJson(@PathVariable("pagenum")int pagenum) {
        System.out.println("----pagenum "+pagenum);
//        调用分页插件
        PageHelper.startPage(pagenum, 5);
        List<Jobs> jobs = jobsService.selectList(new EntityWrapper<Jobs>().orderBy("job_creattime",false));
        PageInfo<Jobs> jobPageInfo=new PageInfo<>(jobs);

        List<JobsStudio> jobsStudios = new ArrayList<JobsStudio>();
//        将值存入构建类
        for (Jobs jobs1 : jobs) {
            Studio studio = studioService.selectOne(new EntityWrapper<Studio>().eq("stu_id", jobs1.getJobStudio()));
            JobsStudio jobsStudio = new JobsStudio();
            jobsStudio.setJobs(jobs1);
            jobsStudio.setStudio(studio);

            Date date1 = jobs1.getJobCreattime();
            Date date = new Date();
//            两个日期相减
            long cha = date.getTime() - date1.getTime();
//            拆分天数，小时，分钟
            long day = cha / (24 * 60 * 60 * 1000);
            long hour = (cha / (60 * 60 * 1000) - day * 24);
            long min = ((cha / (60 * 1000)) - day * 24 * 60 - hour * 60);
            String time = "出错";
            int a = 0;
            //显示天
            if (day > 0) {
                a = (int) day;
                time = a + "天前";
            } else if (day == 0) {
//                显示小时
                if (hour > 0) {
                    a = (int) hour;
                    time = a + "小时前";
                }
                //显示分钟
                else if (hour == 0) {
                    if (min > 0) {
                        a = (int) min;
                        time = a + "分钟前";
                    }
                }
            }
            jobsStudio.setTime(time);
            jobsStudios.add(jobsStudio);

        }
        int num = jobs.size();
        System.out.println("--------num "+num);
        return ResponseBo.ok().put("jobsStudios", jobsStudios).put("num",num).put("pageinfo",jobPageInfo);
    }

    @PostMapping("/search/jNum")
    @ResponseBody
    public ResponseBo jNum() {
        //        获得招聘的个数
        int jNum = jobsService.selectCount(new EntityWrapper<Jobs>());
        return ResponseBo.ok().put("jNum", jNum);
    }

    //    返回搜索到的Json数据
    @PostMapping("/sear")
    public ResponseBo sear(String sear) {
        return ResponseBo.ok();
    }

    //根据搜索职位查找
    @PostMapping("/search/cha/{pagenum}")
    @ResponseBody
    public ResponseBo s(String nei,@PathVariable("pagenum")int pagenum) {
        System.out.println("----nei "+nei);
        System.out.println("----pagenum "+pagenum);
        //根据职位搜索所有的招聘信息
        PageHelper.startPage(pagenum, 8);
        List<Jobs> jobs = jobsService.selectList(new EntityWrapper<Jobs>().like("job_name", nei).orderBy("job_creattime",false));
        System.out.println("++++"+jobs);

        PageInfo<Jobs> jobPageInfo=new PageInfo<>(jobs);

        List<JobsStudio> jobsStudios = new ArrayList<JobsStudio>();
//        将值存入构建类
        for (Jobs jobs1 : jobs) {
            Studio studio = studioService.selectOne(new EntityWrapper<Studio>().eq("stu_id", jobs1.getJobStudio()));
            JobsStudio jobsStudio = new JobsStudio();
            jobsStudio.setJobs(jobs1);
            jobsStudio.setStudio(studio);

            //                调用自定义工具类
            String time = "";
            fartime f = new fartime();
            time = f.far(jobs1.getJobCreattime());

            jobsStudio.setTime(time);
            jobsStudios.add(jobsStudio);
        }

//        根据工作室搜索招聘信息
//        Studio s2 = studioService.selectOne(new EntityWrapper<Studio>().like("stu_name", nei));
//        if (s2 == null) {
//        } else {
////            jobs2: 查询某个工作室的全部招聘信息
//            List<Jobs> jobs2 = jobsService.selectList(new EntityWrapper<Jobs>().like("job_studio", s2.getStuId()));
//
//            for (Jobs j2 : jobs2) {
//                JobsStudio jobsStudio = new JobsStudio();
//                jobsStudio.setJobs(j2);
//                jobsStudio.setStudio(s2);
//
//                String time = "";
////                调用自定义工具类
//                fartime f = new fartime();
//                time = f.far(j2.getJobCreattime());
//                System.out.println("------------+" + f.far(j2.getJobCreattime()));
//
//                jobsStudio.setTime(time);
//                jobsStudios.add(jobsStudio);
//            }
//        }
//
//        PageInfo<JobsStudio> jobPageInfo=new PageInfo<>(jobsStudios);

        int num = jobs.size();
        System.out.println("--------num "+num);
        return ResponseBo.ok().put("jobsStudios", jobsStudios).put("num",num).put("pageinfo",jobPageInfo);
    }

    //根据点击搜索条件返回招聘信息
    @PostMapping("/search/choice/{pagenum}")
    @ResponseBody
    public ResponseBo choice(String tee, String money, String sort,String time,String nei,@PathVariable("pagenum")int pagenum) {
        System.out.println("=====tee: " + tee + "====money: " + money + "====sort: " + sort+"====time: "+time+"=========nei "+nei+"=====pagenum "+pagenum);

        if (tee == null || tee.equals("all")){
            tee = "";
        }
        if (money == null || money.equals("all")) {
            money = "";
        }
        Date date = new Date();
        long s2 = 0;
        if (time.equals("365")){
            s2 = 31536000000L;
        }else if (time.equals("30")){
            s2 =2592000000L;
        }
        else if (time.equals("7")){
            s2 = 201600000;
        }
        else if(time.equals("1")){
            s2 = 28800000;
        }
        long s  = date.getTime();
        s = s -s2;
        date.setTime(s);
//        分页插件
        PageHelper.startPage(pagenum, 8);

        List<Jobs> jobs = new ArrayList<>();

        //        获得要求包含选择信息的job
        if (tee.equals("")) {
            System.out.println("-----tee: " + tee);
            if (money.equals("")) {
                if (time.equals("365")||time.equals("0")){
                    System.out.println("---------time: "+time);
                    jobs = jobsService.selectList(new EntityWrapper<Jobs>().lt("job_creattime",date).like("job_name",nei)
                            .orderBy("job_creattime",false));
                }
                else if (time.equals("30")||time.equals("7")||time.equals("1")){
                    System.out.println("---------time: "+time);
                    jobs = jobsService.selectList(new EntityWrapper<Jobs>().ge("job_creattime",date).like("job_name",nei)
                            .orderBy("job_creattime",false));
                }
            } else if (money.equals("2")) {
                if (time.equals("365")||time.equals("0")){
                    jobs = jobsService.selectList(new EntityWrapper<Jobs>().lt("job_money", 2).lt("job_creattime",date).
                            like("job_name",nei).orderBy("job_creattime",false));
                }
                else if (time.equals("30")||time.equals("7")||time.equals("1")){
                    jobs = jobsService.selectList(new EntityWrapper<Jobs>().lt("job_money", 2).gt("job_creattime",date)
                            .like("job_name",nei).orderBy("job_creattime",false));
                }



            } else if (money.equals("5")) {
                if (time.equals("365")||time.equals("0")){
                    jobs = jobsService.selectList(new EntityWrapper<Jobs>().ge("job_money", 2)
                            .lt("job_money", 5).lt("job_creattime",date).orderBy("job_creattime",false));
                }
                else if (time.equals("30")||time.equals("7")||time.equals("1")){
                    jobs = jobsService.selectList(new EntityWrapper<Jobs>().ge("job_money", 2)
                            .lt("job_money", 5).gt("job_creattime",date).orderBy("job_creattime",false));
                }

            } else if (money.equals("8")) {
                if (time.equals("365")||time.equals("0")){
                    jobs = jobsService.selectList(new EntityWrapper<Jobs>().ge("job_money", 5)
                            .lt("job_money", 8).lt("job_creattime",date).orderBy("job_creattime",false));
                }
                else if (time.equals("30")||time.equals("7")||time.equals("1")){
                    jobs = jobsService.selectList(new EntityWrapper<Jobs>().ge("job_money", 5)
                            .lt("job_money", 8).gt("job_creattime",date).orderBy("job_creattime",false));
                }

            } else if (money.equals("10")) {
                if (time.equals("365")||time.equals("0")){
                    jobs = jobsService.selectList(new EntityWrapper<Jobs>().ge("job_money", 8)
                            .lt("job_money", 10).lt("job_creattime",date).orderBy("job_creattime",false));
                }
                else if (time.equals("30")||time.equals("7")||time.equals("1")){
                    jobs = jobsService.selectList(new EntityWrapper<Jobs>().ge("job_money", 8)
                            .lt("job_money", 10).gt("job_creattime",date).orderBy("job_creattime",false));
                }

            } else if (money.equals("10up")) {
                if (time.equals("365")||time.equals("0")){
                    jobs = jobsService.selectList(new EntityWrapper<Jobs>().ge("job_money", 10)
                            .lt("job_creattime",date).orderBy("job_creattime",false));
                }
                else if (time.equals("30")||time.equals("7")||time.equals("1")){

                    jobs = jobsService.selectList(new EntityWrapper<Jobs>().ge("job_money", 10)
                            .gt("job_creattime",date).orderBy("job_creattime",false));
                }

            }
        } else {
            if (money.equals("")) {
                if (time.equals("365")||time.equals("0")){
                    jobs = jobsService.selectList(new EntityWrapper<Jobs>().like("job_require", tee)
                            .lt("job_creattime",date).orderBy("job_creattime",false));
                    System.out.println("++++"+jobs);
                    System.out.println("------here");
                }
                else if (time.equals("30")||time.equals("7")||time.equals("1")){
                    jobs = jobsService.selectList(new EntityWrapper<Jobs>().like("job_require", tee)
                            .gt("job_creattime",date).orderBy("job_creattime",false));
                }

            } else if (money.equals("2")) {
                if (time.equals("365")||time.equals("0")){
                    jobs = jobsService.selectList(new EntityWrapper<Jobs>().like("job_require", tee)
                            .lt("job_money", 2).lt("job_creattime",date).orderBy("job_creattime",false));
                }
                else if (time.equals("30")||time.equals("7")||time.equals("1")){
                    jobs = jobsService.selectList(new EntityWrapper<Jobs>().like("job_require", tee)
                            .lt("job_money", 2).gt("job_creattime",date).orderBy("job_creattime",false));
                }

            } else if (money.equals("5")) {
                if (time.equals("365")||time.equals("0")){
                    jobs = jobsService.selectList(new EntityWrapper<Jobs>().like("job_require", tee)
                            .ge("job_money", 2).lt("job_money", 5).lt("job_creattime",date)
                            .orderBy("job_creattime",false));
                }
                else if (time.equals("30")||time.equals("7")||time.equals("1")){
                    jobs = jobsService.selectList(new EntityWrapper<Jobs>().like("job_require", tee)
                            .ge("job_money", 2).lt("job_money", 5).gt("job_creattime",date)
                            .orderBy("job_creattime",false));
                }

            } else if (money.equals("8")) {
                if (time.equals("365")||time.equals("0")){
                    jobs = jobsService.selectList(new EntityWrapper<Jobs>().like("job_require", tee)
                            .ge("job_money", 5).lt("job_money", 8).lt("job_creattime",date)
                            .orderBy("job_creattime",false));
                }
                else if (time.equals("30")||time.equals("7")||time.equals("1")){
                    jobs = jobsService.selectList(new EntityWrapper<Jobs>().like("job_require", tee)
                            .ge("job_money", 5).lt("job_money", 8).gt("job_creattime",date)
                            .orderBy("job_creattime",false));
                }

            } else if (money.equals("10")) {
                if (time.equals("365")||time.equals("0")){
                    jobs = jobsService.selectList(new EntityWrapper<Jobs>().like("job_require", tee)
                            .ge("job_money", 8).lt("job_money", 10).lt("job_creattime",date)
                            .orderBy("job_creattime",false));
                }
                else if (time.equals("30")||time.equals("7")||time.equals("1")){
                    jobs = jobsService.selectList(new EntityWrapper<Jobs>().like("job_require", tee)
                            .ge("job_money", 8).lt("job_money", 10).gt("job_creattime",date)
                            .orderBy("job_creattime",false));
                }

            } else if (money.equals("10up")) {
                if (time.equals("365")||time.equals("0")){
                    jobs = jobsService.selectList(new EntityWrapper<Jobs>().like("job_require", tee)
                            .ge("job_money", 10).lt("job_creattime",date)
                            .orderBy("job_creattime",false));
                }
                else if (time.equals("30")||time.equals("7")||time.equals("1")){
                    jobs = jobsService.selectList(new EntityWrapper<Jobs>().like("job_require", tee)
                            .ge("job_money", 10).gt("job_creattime",date)
                            .orderBy("job_creattime",false));
                }

            }

        }


        List<JobsStudio> jobsStudios = new ArrayList<JobsStudio>();
        for (Jobs jobs1 : jobs) {
            Studio studio = studioService.selectOne(new EntityWrapper<Studio>().eq("stu_id", jobs1.getJobStudio()));
            JobsStudio jobsStudio = new JobsStudio();
            jobsStudio.setJobs(jobs1);
            jobsStudio.setStudio(studio);

            // 调用自定义工具类
            String t1 = "";
            fartime f = new fartime();
            t1 = f.far(jobs1.getJobCreattime());

            jobsStudio.setTime(t1);
            jobsStudios.add(jobsStudio);
        }

        if (sort.equals("grade")) {
            //排序
            for (int i = 1; i < jobsStudios.size(); i++) {
                for (int j = 0; j < jobsStudios.size() - 1; j++) {
                    JobsStudio j1 = jobsStudios.get(j);
                    JobsStudio j2 = jobsStudios.get(j + 1);
//                System.out.println("==== "+j1.getStudio().getStuGrade()+"========="+j2.getStudio().getStuGrade());
                    if (j1.getStudio().getStuGrade() < j2.getStudio().getStuGrade()) {
                        jobsStudios.set(j, j2);
                        jobsStudios.set(j + 1, j1);
                    }
                }
            }
        } else if (sort.equals("gradn")) {
            for (int i = 1; i < jobsStudios.size(); i++) {
                for (int j = 0; j < jobsStudios.size() - 1; j++) {
                    JobsStudio j1 = jobsStudios.get(j);
                    JobsStudio j2 = jobsStudios.get(j + 1);
//                System.out.println("==== "+j1.getStudio().getStuGrade()+"========="+j2.getStudio().getStuGrade());
                    if (j1.getStudio().getStuProjectnum() < j2.getStudio().getStuProjectnum()) {
                        jobsStudios.set(j, j2);
                        jobsStudios.set(j + 1, j1);
                    }
                }
            }
        } else if (sort.equals("gradpn")) {
            for (int i = 1; i < jobsStudios.size(); i++) {
                for (int j = 0; j < jobsStudios.size() - 1; j++) {
                    JobsStudio j1 = jobsStudios.get(j);
                    JobsStudio j2 = jobsStudios.get(j + 1);
                    if (j1.getStudio().getStuMembernum() < j2.getStudio().getStuMembernum()) {
                        jobsStudios.set(j, j2);
                        jobsStudios.set(j + 1, j1);
                    }
                }
            }
        }
        int num = jobs.size();
        System.out.println("--------num "+num);
        PageInfo<Jobs> jobPageInfo=new PageInfo<>(jobs);
        return ResponseBo.ok().put("jobsStudios", jobsStudios).put("num",num).put("pageinfo",jobPageInfo);
    }

    //    工作详情页面
    @GetMapping("/mes/{id}")
    public String mes(@PathVariable("id") int id, Model model) {
//        System.out.println("======== id:"+id);
        model.addAttribute("id", id);
        System.out.println("555");
        return "jobs/detail";
//        return "jobs/helloworld";
    }

    //    返回渲染detail的json
    @PostMapping("/mes/{id}")
    @ResponseBody
    public ResponseBo mes2(@PathVariable("id") int id) {
//            获取当前登录的用户
        AbstractController a = new AbstractController();
        Alluser alluser = new Alluser();
        getAlluser g = new getAlluser();
        alluser = g.aa();
        System.out.println("----alluser "+alluser);


//        获取user id
        String userid = alluser.getAllId();
        Users users = usersService.selectById(userid);
        System.out.println("users-"+users);

        //判断该用户是否有工作室
        String join = "no";
        if(users.getUserStudio()!=null){
            join = "yes";
//            System.out.println("已有工作室");
        }


//        获取招聘表
        Jobs jobs = jobsService.selectById(id);
//        System.out.println("========= " + jobs);
//        根据招聘id获取工作室表
        Studio studio = studioService.selectOne(new EntityWrapper<Studio>().eq("stu_id", jobs.getJobStudio()));
        JobsStudio js = new JobsStudio();
        js.setJobs(jobs);
        js.setStudio(studio);
        return ResponseBo.ok().put("js", js).put("join",join);
    }

    //    上传简历
    @PostMapping("/upload")
//    @RequiresRoles("users")
    public String submit(int jid, resume r, HttpServletRequest request,Model model)throws Exception{
//        System.out.println("jid: "+jid);
        //如果文件不为空
        if (!r.getResume().isEmpty()){
            FileController f = new FileController();
            List<String> list = new ArrayList<>();
//            调用工具类将文件存入指定文件夹
//            list = f.fileupload(r.getResume(),"D:/ChuangKeFile/resume");
            list = f.fileupload(r.getResume(), ConstCofig.RootPath+"resume");

//            获取加上uuid的文件名字
            String fileurl = list.get(0);
            String entryName = list.get(1);

//            jobuser赋值
            Jobuser j = new Jobuser();
            Date date = new Date();
            j.setJuTime(date);
            j.setJuFile(fileurl);
            j.setJuState("审核中");
            j.setJuJobs(jid);

//            获取当前登录的用户
            AbstractController a = new AbstractController();
            Alluser alluser = new Alluser();
            getAlluser g = new getAlluser();
            alluser = g.aa();
            j.setJuUsers(alluser.getAllId());

            boolean i = jobuserService.insert(j);
        }

        return "redirect:/ForJob/mes/"+jid;
    }

    @GetMapping("/test")
    public String test(){
        System.out.println("test111");
        return "redirect:/ForJob/search";
    }
}















