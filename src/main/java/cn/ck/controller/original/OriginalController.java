package cn.ck.controller.original;

import cn.ck.entity.*;

import cn.ck.entity.bean.OriCol;
import cn.ck.entity.bean.OriColUser;
import cn.ck.entity.bean.OriUser;
import cn.ck.service.*;
import cn.ck.utils.ResponseBo;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.models.auth.In;
import org.apache.commons.io.IOUtils;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import cn.ck.controller.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

@Controller
@RequestMapping("/original")
public class OriginalController extends AbstractController{
        @Autowired
        UsersService usersService;
        @Autowired
        OriginalService originalService;
        @Autowired
        CollectoriService collectoriService;
        @Autowired
        AlluserService alluserService;
        @Autowired
        GradeoriService gradeoriService;

        int start=1;//记录当前页 默认为“1“
        int size = 4; //记录每页条目数，默认为”10“
        String key;
        String type;
        String tag;


        @RequestMapping("/all")
        public String all(@RequestParam(value = "type", defaultValue = "") String type,@RequestParam(value = "tag",defaultValue = "")  String tag,
                          @RequestParam(value = "start", defaultValue = "1")  int start){
            this.type=type;
            this.tag=tag;
            this.start = start;
                return "original/original";
        }


        @RequestMapping("/search")
        public String search(@RequestParam(value = "key",required = true) String key,@RequestParam(value = "start", defaultValue = "0")  int start){
            this.start=start;
            this.key=key;
            return "original/ori_search_results";
        }

       @RequestMapping("/details/{id}")
        public String details(@PathVariable("id")Integer oriId){
                return "original/ori_details";
        }



        //改变页数（start）
        @RequestMapping("/allpage")
        public String allpage(@RequestParam(value = "type", defaultValue = "") String type,@RequestParam(value = "tag",defaultValue = "")  String tag,
                @RequestParam(value = "start", defaultValue = "1")  int start){
            System.out.println(tag);
            this.type=type;
            this.tag=tag;
            this.start = start;
            return "original/original";
        }

    @RequestMapping("/searchPage")
    public String searchpage(
            @RequestParam(value = "start", defaultValue = "1")  int start){
        this.start = start;

        return "original/ori_search_results";
    }

        @RequestMapping("/all1")
        @ResponseBody
        public ResponseBo index(){

                PageHelper.startPage(start, size);
                int id=1;
                List<OriUser> originals = originalService.selectAllOri(type,tag,id);
            /*for (OriUser oriUser:originals) {
                    String[] strings=oriUser.getOrigTag().split(",");
                System.out.println(strings.length);
            }*/


                PageInfo<OriUser> page = new PageInfo<>(originals);
            return ResponseBo.ok().put("type",type).put("tag",tag).put("page",page);
        }


        @RequestMapping("getDetails")
        @ResponseBody
        public ResponseBo getdetails(Integer origId) throws Exception{
            int grade=0;
            int id=1;
                if(origId == null) return ResponseBo.error("无法找到此项目");

                boolean isLogined = false, isCollected = false, isScored = false;
                if(getUser() != null){
                        isLogined = true;
                        isCollected = collectoriService.isCollected(getUser().getAllId(), origId);
                        isScored = gradeoriService.isScored(getUser().getAllId(), origId);
                }
            String[] tags=originalService.selectOriUser(origId).getOrigTag().split(",");

            List<OriUser> originals=new ArrayList<>();
            for(int i=0;i<tags.length;i++) {

                originals.addAll(originalService.selectOther(origId,tags[i]));
                System.out.println(originals);
            }
            if(originals.size()>=4)
                originals=originals.subList(0,3);
        else if(originals.size()==0)
            originals=originalService.selectAllOri(type,tag,id).subList(0,3);

if (isScored){
    grade=gradeoriService.selectOne(new EntityWrapper<Gradeori>().eq("grao_user",getUser().getAllId()).eq("grao_ori",origId)).getGaroSco();
}


                return ResponseBo.ok()
                        .put("curOri", originalService.selectOriUser(origId))
                        .put("grade",grade)
                        .put("isLogined", isLogined)
                        .put("isCollected", isCollected)
                        .put("other",originals)
                        .put("isScored", isScored);


        }

    @RequestMapping("insertColOri")
    @ResponseBody
    public ResponseBo insertColOri(String oriId) throws Exception{

            Collectori collectori=new Collectori();
            collectori.setColoTime(new Date());
            collectori.setColoOgi(Integer.parseInt(oriId));
            collectori.setColoUsers(getUser().getAllId());
            if(collectoriService.insert(collectori))
                return ResponseBo.ok();
            else
                return ResponseBo.error("收藏失败");
    }

    @RequestMapping("deleteColOri")
    @ResponseBody
    public ResponseBo deleteColOri(String oriId) throws Exception{
        if(collectoriService.delete(new EntityWrapper<Collectori>().eq("colo_users",getUser().getAllId()).eq("colo_ogi",Integer.parseInt(oriId))))
            return ResponseBo.ok();
        else
            return ResponseBo.error("取消收藏失败");
    }

    @RequestMapping("insertScore")
    @ResponseBody
    public Boolean insertScore(Integer origId,int score){
            Gradeori gradeori =new Gradeori();
            gradeori.setGraoUser(getUser().getAllId());
            gradeori.setGraoOri(origId);
            gradeori.setGaroSco(score);
            List<Gradeori> gradeoris=gradeoriService.selectList(new EntityWrapper<Gradeori>().eq("grao_ori",origId));
            Original original=originalService.selectOne(new EntityWrapper<Original>().eq("orig_id",origId));
            Double grade=original.getOrigGrade();
            Double score1=((gradeoris.size()*grade)+score)/(gradeoris.size()+1);
            original.setOrigGrade(score1);
            originalService.updateAllColumnById(original);
            return gradeoriService.insert(gradeori);

    }


    @RequestMapping("result")
    @ResponseBody
    public Map<String,Object> result(){
            Map<String,Object> map = new HashMap<>();
        PageHelper.startPage(start,6);
        List<OriUser> list =  originalService.selectSearch(key);

        PageInfo<OriUser> page=new PageInfo<>(list);
        map.put("key",key);
        map.put("result",page);
        return map;
    }

    /**
     * 文件下载
     * @param fileName 文件的名字
     */
    //这是一个下载的函数
    @RequestMapping(value = "/down",method = RequestMethod.GET)
    public void download(HttpServletRequest request, HttpServletResponse response, String fileName) {
        System.out.println("in--down---and--filename--is:"+fileName);
        try {
            //存放地址
            String realPath = "E:\\ChuangKeFile\\Ori";
            //获得服务器端某个文件的完整路径
            String fullPath = realPath + File.separator + fileName;
            //设置响应
            response.setContentType("application/force-download");
            //设置响应头信息
            response.setHeader("Content-Disposition", "attachment;fileName="+fileName);// 设置文件名
            //文件名有中文时设置编码
            response.setHeader("Content-Disposition", "attachment;filename="+new String(fileName.getBytes("GBK"),"ISO-8859-1"));

            File downloadFile = new File(fullPath);
            FileInputStream inputStream = new FileInputStream(downloadFile);
            OutputStream outputStream =  response.getOutputStream();
            IOUtils.copy(inputStream, outputStream);
            response.flushBuffer();
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

/*    @RequestMapping("insertColOri")
    @ResponseBody
    public Boolean insertColOri(String oriId,String isCol) throws Exception{
        Boolean isCol1;
        if(isCol.equals("true"))
            isCol1=true;
        else
            isCol1=false;
        if(!isCol1) {
            Collectori collectori = new Collectori();
            collectori.setColoTime(new Date());
            collectori.setColoOgi(Integer.parseInt(oriId));
            collectori.setColoUsers(getUser().getAllId());
            if (collectoriService.insert(collectori)) {
                isCol1=true;
                return isCol1;
            }
            else{
                return isCol1;
            }
        }else{
            if(collectoriService.delete(new EntityWrapper<Collectori>().eq("colo_users",getUser().getAllId()).eq("colo_ogi",Integer.parseInt(oriId))))
            {
                isCol1=false;
                return isCol1;
            }
            else
            {
                return isCol1;
            }
        }
    }*/


    /*@RequestMapping("download")
    @ResponseBody
    public ResponseBo download(String orid){
        EntityWrapper<Original> entityWrapper = new EntityWrapper<Original>().eq
        return collectoriService.selectById()
    }*/

    /*@RequestMapping("getOther")
    @ResponseBody
    public ResponseBo getOther(String origType,String origTag){
        List<Original> list = originalService.selectList(new EntityWrapper<Original>().eq("orig_type"))
    }*/

/*    @RequestMapping("ScoreOri/{origId}")
        @ResponseBody
        public ResponseBo scoreOriginal(@PathVariable("origId")Integer origId){
                Alluser alluser = getUser();
                if(alluser == null) return ResponseBo.error("未登录");
                //取出视频记录
                Original original = originalService.selectById(origId);
                if(original == null) return ResponseBo.error("找不到项目");
                //插入点赞记录
                 Gradeori score = new Gradeori();
                score.setGraoOri(origId);
                score.setGraoUser(alluser.getAllId());
                gradeoriService.insert(score);
           //更新视频点赞人数

        return ResponseBo.ok("评分成功");
        }

    @RequestMapping("tuijie")
    @ResponseBody
    public List<Original> tuijie(String origTag){
        String[] tags=origTag.split(",");
        List<Original> originals=new ArrayList<>();
        for(int i=0;i<tags.length;i++) {
            originals.addAll(originalService.selectList(new EntityWrapper<Original>().like("orig_name",tags[i])));
        }
        originals=originals.subList(2,3);
        return originals;
    }*/


}
