package com.nwafu.PISMDB.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nwafu.PISMDB.UserRepository;
import com.nwafu.PISMDB.entity.*;
import com.nwafu.PISMDB.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

//@RestController
@Controller
@Slf4j
@Api(description = "分子操作Api")
public class CompoundsController {

    private String search_text;  //临时存放待搜索的文本

    @Autowired
    private CompoundsService compoundsService;
    @Autowired
    private TargetsService targetsService;
    @Autowired
    private PathwaysService pathwaysService;
    @Autowired
    private LuceneSearchService luceneSearchService;
    @Autowired
    private FileSearchService fileSearchService;

    @Autowired // This means to get the bean called userRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private UserRepository userRepository;

    /////////////////文件搜索部分开始/////////////////////
    /**
     * 这里是上传文件进行文件搜索
     * @param uploadFile
     * @param request
     * @return
     * @throws IOException
     */
    //这个功能还没完，缺少blast的东西

    @ApiOperation(value = "上传文件搜索", notes = "根据本地的文件搜索")
    @PostMapping("/uploadFileSearch")
    //@RequestParam("uploadFile")MultipartFile uploadFile
    public String uploadfile(MultipartFile uploadFile, HttpServletRequest request) throws IOException {
        String fileName = uploadFile.getOriginalFilename();
        if(null == fileName){
            System.out.println("找不到文件");
            return "找不到文件";
        }
//        String file_path = request.getParameter("uploadFile");
        if(!fileName.split(".")[1].equals(".mol2")){
            System.out.println("文件格式错误");
            return null;
        }
        String newFileName = fileName;
        String path = "D:\\upload_file_list\\";     /**
                                                            这里上传路径要改*/
        uploadFile.transferTo(new File(path+newFileName));
        System.out.println("上传成功！");
        fileSearchService.fileSearch(path+fileName,newFileName.split("\\.")[0]);
        return "Search-sequence-Result";

        /**
         * 上传成功后还应该调用搜索引擎的更新函数和blast+库的重建函数
         */

    }

//////////////文件搜索部分结束///////////////////////////


    /**
     * 分页获取邮箱为指定内容的数据
     *
     * @return
     */
    ///////////搜索引擎部分开始/////////////////////

    @ApiOperation(value = "创建搜索索引", notes = "创建搜索引擎的索引,不要随便调用")
    @RequestMapping(value = "/createIndex",method = RequestMethod.POST)
    @ResponseBody
    public Integer createIndex() throws Exception {
        log.info("创建搜索引擎中");
        Integer result = luceneSearchService.createIndex();
        return result;
    }
    //content中的值  id,chemicalNames,IUPAC_Name,ChemicalFormular,

    @ApiOperation(value = "搜索引擎关键字查找传参", notes = "搜索引擎关键字查找传参")
    @GetMapping("/keywordSearch")
    @ResponseBody
    public String searchIndex(@RequestParam String callback,@RequestParam String search_text) throws Exception {
        System.out.println(search_text);
        log.info("搜索引擎关键字查找传参：{}",search_text);
//        List<Compounds> compoundsList = luceneSearchService.searchIndex("[^\\S]*" + search_text + "*");
        List<FormatData> compoundsList = luceneSearchService.searchIndex(search_text + "*");
//        List<Compounds> compoundsList = luceneSearchService.searchIndex(search_text);
//        return compoundsList;
        CallbackResult<List<FormatData>> result = new CallbackResult();
        result.setCallback(callback);
        result.setData(compoundsList);
        log.info("{}",result.changToJsonp());
        return result.changToJsonp();
    }

    @ApiOperation(value = "搜索结果", notes = "搜索结果")
    @RequestMapping(value = "/search_result",method = RequestMethod.GET)
    public String xianshi(HttpServletRequest request) {
        search_text = request.getParameter("search_text");
        System.out.println(search_text);
        return "Search-sequence-Result";
    }

    /////////////////////搜索引擎部分结束////////////////////////

    @ApiOperation(value = "分页查询", notes = "分页查询，目前有问题")
    @GetMapping("/listCompounds")
    public PageInfo listCompounds(Model m, @RequestParam(value = "start", defaultValue = "0") int start, @RequestParam(value = "size", defaultValue = "5") int size) throws Exception {
        //1. 在参数里接受当前是第几页 start ，以及每页显示多少条数据 size。 默认值分别是0和5。
        //2. 根据start,size进行分页，并且设置id 倒排序
        PageHelper.startPage(start, size);
        //3. 因为PageHelper的作用，这里就会返回当前分页的集合了
        List<Compounds> cs = compoundsService.findById();
        //4. 根据返回的集合，创建PageInfo对象
        PageInfo<Compounds> page = new PageInfo<Compounds>(cs);
        //5. 把PageInfo对象扔进model，以供后续显示
        m.addAttribute("page", page);
//        //6. 跳转到listCategory.jsp
        return page;
    }

    @ApiOperation(value = "查询分子数据格式化", notes = "查询分子数据格式化")
    @GetMapping("/browse-C")
    @ResponseBody
    public String showCompoundsPathway(@RequestParam String callback) {
        List<CompoundsBasic> list1 = compoundsService.FindBasic();
        List<Compounds> listc=compoundsService.findById();
        List<CompoundsPathway> list2 = compoundsService.FindPathway();
      //  List<CompoundsRelatedCompounds> list3 = compoundsService.FindRelatedCompounds();
        List<CompoundSupportingInformation> list4 = compoundsService.FindSupportingInformation();
        List<FormatData> list =new ArrayList<>();
        for(int i=0;i<compoundsService.getCompoundsCount();i++){
        FormatData formatData=new FormatData();
        CompoundsRelatedCompounds compoundsRelatedCompounds=new CompoundsRelatedCompounds();
        formatData.setId(list1.get(i).getPISMID());
        formatData.setIdLink(list1.get(i).getPISMID());
        formatData.setName(listc.get(i).getChemicalNames());
        formatData.setImgurl(listc.get(i).getStructure());
        formatData.setBasic(list1.get(i));
        compoundsRelatedCompounds.setPISMID(list1.get(i).getPISMID());
        compoundsRelatedCompounds.setCompoundsList(compoundsService.findRelatedById(list1.get(i).getPISMID()));
        formatData.setRelated(compoundsRelatedCompounds);
        formatData.setPathway(list2.get(i));
        formatData.setSupporting(list4.get(i));
        list.add(formatData);
        }
        CallbackResult<List<FormatData>> result = new CallbackResult();
        result.setCallback(callback);
        result.setData(list);
        log.info("{}",result.changToJsonp());
        return result.changToJsonp();
    }


    @ApiOperation(value = "查分子的相关分子测试", notes = "查分子的相关分子测试")
    @GetMapping("/related")
    @ResponseBody
    public String related(@RequestParam String callback,@Param("pismid")String pismid){
        List<String> stringList= compoundsService.findRelatedById(pismid);
        CallbackResult<List<String>> result = new CallbackResult();
        result.setCallback(callback);
        result.setData(stringList);
        log.info("{}",result.changToJsonp());
        return result.changToJsonp();
    }


    @ApiOperation(value = "系统首页", notes = "跳转到系统的首页")
    @GetMapping("/index")
    public String getCount(HttpServletRequest request) {
        request.setAttribute("compoundsCount", compoundsService.getCompoundsCount());
        request.setAttribute("targetsCount", targetsService.getTargetsCount());
        request.setAttribute("pathwaysCount", pathwaysService.getPathwaysCount());
        return "index";
    }

    @ApiOperation(value = "跳转到分子页面", notes = "跳转到分子页面")
    @GetMapping("/Browse_C")
    public String Browse_C() {
        return "browse-Compound";
    }


    @ApiOperation(value = "跳转到Search_text页面", notes = "跳转到Search_text页面")
    @GetMapping("/Search_text")
    public String Search_text() {
        return "search-text";
    }



//        @GetMapping("/compounds")
//    public String greetingForm(Model model) {
//        model.addAttribute("compounds", new Compounds());
//        System.out.println("上传数据1"+ model.toString());
//
//        return "compounds";
//}
    @ApiOperation(value = "文件上传跳转接口", notes = "跳转到文件上传")
    @RequestMapping(value = "/greeting",method = RequestMethod.GET)
    public String greetingForm123(Model model) {
        model.addAttribute("compounds", new Compounds());
        return "upload";
    }

    static int pid=1220;
    @ApiOperation(value = "文件上传", notes = "文件上传到数据库")
    @RequestMapping(value = "/upload_data", method = RequestMethod.POST)
    public String greetingSubmit(@ModelAttribute Compounds compounds/*HttpServletRequest request*/) {
        Compounds newCompounds = new Compounds();
        newCompounds.setPISMID("PISM0"+String.valueOf(pid));
//        newCompounds.setVersion(compounds.getVersion());
        newCompounds.setChemicalNames(compounds.getChemicalNames());
        newCompounds.setIUPAC_Name(compounds.getIUPAC_Name());
        newCompounds.setSmiles(compounds.getSmiles());
        pid++;
        System.out.println(newCompounds.getPISMID());
//        System.out.println(compounds.getChemicalNames());
//        System.out.println(compounds.getSmiles());
        //newCompounds.setSmiles(compounds.getSmiles());
//      newCompounds.setPISMID(request.getParameter("PISMID"));
        userRepository.save(newCompounds);
        //System.out.println("上传数据2"+ newCompounds.getPISMID());
        return "result";

    }

    @ApiOperation(value = "获取所有数据", notes = "获取所有数据")
    @GetMapping("/all")
    public String getMessage(Model model) {

        Iterable<Compounds> compounds = userRepository.findAll();

        model.addAttribute("compounds", compounds);
        System.out.println("上传数据3" + compounds.toString());
        return "all";
    }

    @ApiOperation(value = "获取所有文献", notes = "获取所有文献")
    @GetMapping("/allReference")
    @ResponseBody
    public String allReference(@RequestParam String callback) {
        List<Reference> referenceList= compoundsService.findReference();
        CallbackResult<List<Reference>> result = new CallbackResult();
        result.setCallback(callback);
        result.setData(referenceList);
        log.info("{}",result.changToJsonp());
        return result.changToJsonp();
    }

    @ApiOperation(value = "根据PISMID获取分子详细信息", notes = "根据PISMID获取分子详细信息")
    @GetMapping("/compoundInfomationByPismid")
    @ResponseBody
    public String compoundInfomation(@RequestParam String callback,@Param("pismid")String pismid) {
        Compounds compoundsList=compoundsService.findByPISMID(pismid);
        CallbackResult<Compounds> result = new CallbackResult();
        result.setCallback(callback);
        result.setData(compoundsList);
        return result.changToJsonp();
    }

}

