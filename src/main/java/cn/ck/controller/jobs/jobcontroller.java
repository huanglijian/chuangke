package cn.ck.controller.jobs;

import cn.ck.controller.AbstractController;
import cn.ck.entity.Alluser;
import cn.ck.entity.Jobs;
import cn.ck.entity.Studio;
import cn.ck.entity.bean.JobsStudio;
import cn.ck.mapper.JobsMapper;
import cn.ck.service.JobsService;
import cn.ck.service.StudioService;
import cn.ck.utils.ResponseBo;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mysql.jdbc.SocketMetadata;
import org.quartz.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/ForJob")
//@ResponseBody
public class jobcontroller extends AbstractController {
    @Autowired
    private JobsService jobsService;
    @Autowired
    private StudioService studioService;

    //    @RequestMapping("/search")
    @GetMapping("/search")
    public String search() {
        Jobs jobs = new Jobs();
//        获得所有的招聘信息
        List<Jobs> jobss = jobsService.selectList(new EntityWrapper<Jobs>());
//        获得招聘的个数
        int jNum = jobsService.selectCount(new EntityWrapper<Jobs>());
        return "jobs/search";
    }

    @PostMapping("/search/jobs")
    @ResponseBody
    public ResponseBo searchJson() {
        //        获得所有的招聘信息
        List<Jobs> jobs = jobsService.selectList(new EntityWrapper<Jobs>());
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
        return ResponseBo.ok().put("jobsStudios", jobsStudios);
    }

    @PostMapping("/search/jNum")
    @ResponseBody
    public ResponseBo jNum() {
        //        获得招聘的个数
        int jNum = jobsService.selectCount(new EntityWrapper<Jobs>());
        return ResponseBo.ok().put("jNum", jNum);
    }

    //    工作详情页面
    @GetMapping("/mes/{id}")
    public String mes(@PathVariable("id") int id, Model model) {
//        System.out.println("======== id:"+id);
        model.addAttribute("id", id);
        return "jobs/detail";
    }

    //    返回渲染detail的json
    @PostMapping("/mes/{id}")
    @ResponseBody
    public ResponseBo mes2(@PathVariable("id") int id) {
//        获得jobs对象
//        Jobs jobs = jobsService.selectList(new EntityWrapper<Jobs>().eq("jobId",jobId))
//        System.out.println(id);
//        获取招聘表
        Jobs jobs = jobsService.selectById(id);
        System.out.println("========= " + jobs);
//        根据招聘id获取工作室表
        Studio studio = studioService.selectOne(new EntityWrapper<Studio>().eq("stu_id", jobs.getJobStudio()));
        JobsStudio js = new JobsStudio();
        js.setJobs(jobs);
        js.setStudio(studio);
        return ResponseBo.ok().put("js", js);
    }

    //    返回搜索到的Json数据
    @PostMapping("/sear")
    public ResponseBo sear(String sear) {
        System.out.println("====== " + sear);

        return ResponseBo.ok();
    }


    //根据搜索职位查找
    @PostMapping("/search/cha")
    @ResponseBody
    public ResponseBo s(String nei) {
        //根据职位搜索所有的招聘信息
        List<Jobs> jobs = jobsService.selectList(new EntityWrapper<Jobs>().like("job_name", nei));
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

//        根据工作室搜索招聘信息
        Studio s2 = studioService.selectOne(new EntityWrapper<Studio>().like("stu_name",nei));
        if(s2==null){
            System.out.println("空");
        }
        else{
            System.out.println("非空");
//            jobs2: 查询某个工作室的全部招聘信息
            List<Jobs> jobs2 = jobsService.selectList(new EntityWrapper<Jobs>().like("job_studio", s2.getStuId()));
            for (Jobs j2:jobs2) {
                JobsStudio jobsStudio = new JobsStudio();
                jobsStudio.setJobs(j2);
                jobsStudio.setStudio(s2);

                Date date1 = j2.getJobCreattime();
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
        }

        return ResponseBo.ok().put("jobsStudios", jobsStudios);
    }
}














