<template>
  <div class="home" style="padding: 10px">
    <!-- 搜索-->
    <div style="margin: 10px 0">
      <el-form inline="true" size="small">
        <el-form-item label="房源编号">
          <el-input v-model="search1" placeholder="请输入房源编号" clearable>
            <template #prefix
              ><el-icon class="el-input__icon"><search /></el-icon
            ></template>
          </el-input>
        </el-form-item>
        <el-form-item label="房源名称">
          <el-input v-model="search2" placeholder="请输入房源名称" clearable>
            <template #prefix
              ><el-icon class="el-input__icon"><search /></el-icon
            ></template>
          </el-input>
        </el-form-item>
        <el-form-item label="购房者" v-if="user.role == 1">
          <el-input
            v-model="search3"
            placeholder="请输入购房者者昵称"
            clearable
          >
            <template #prefix
              ><el-icon class="el-input__icon"><search /></el-icon
            ></template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            style="margin-left: 1%"
            @click="load"
            size="mini"
            >查询</el-button
          >
        </el-form-item>
        <el-form-item>
          <el-button size="mini" type="danger" @click="clear">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <!-- 按钮-->
    <div style="margin: 10px 0">
      <el-popconfirm
        title="确认删除?"
        @confirm="deleteBatch"
        v-if="user.role == 1"
      >
        <template #reference>
          <el-button type="danger" size="mini">批量删除</el-button>
        </template>
      </el-popconfirm>
    </div>
    <!-- 数据字段-->
    <el-table
      :data="tableData"
      stripe
      border="true"
      @selection-change="handleSelectionChange"
    >
      <el-table-column v-if="user.role == 1" type="selection" width="55">
      </el-table-column>
      <el-table-column prop="isbn" label="房源编号" sortable />
      <el-table-column prop="houseName" label="房源名称" />
      <el-table-column prop="nickName" label="购房者" />
      <el-table-column prop="lendtime" label="成交时间" />
      <el-table-column fixed="right" label="操作">
        <template v-slot="scope">
          <el-button
            size="mini"
            @click="handleEdit(scope.row)"
            v-if="user.role == 1"
            >修改</el-button
          >
          <el-popconfirm
            title="确认删除?"
            @confirm="handleDelete(scope.row)"
            v-if="user.role == 1"
          >
            <template #reference>
              <el-button type="danger" size="mini">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <!--    分页-->
    <div style="margin: 10px 0">
      <el-pagination
        v-model:currentPage="currentPage"
        :page-sizes="[5, 10, 20]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      >
      </el-pagination>

      <el-dialog v-model="dialogVisible2" title="修改房源信息" width="30%">
        <el-form :model="form" label-width="120px">
          <el-form-item label="房源编号">
            <el-input style="width: 80%" v-model="form.isbn"></el-input>
          </el-form-item>
          <el-form-item label="房源名称">
            <el-input style="width: 80%" v-model="form.houseName"></el-input>
          </el-form-item>
          <el-form-item label="购房者">
            <el-input style="width: 80%" v-model="form.nickName"></el-input>
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="dialogVisible2 = false">取 消</el-button>
            <el-button type="primary" @click="save">确 定</el-button>
          </span>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script>
// @ is an alias to /src
import request from "../utils/request";
import { ElMessage } from "element-plus";
import moment from "moment";
export default {
  created() {
    let userStr = sessionStorage.getItem("user") || "{}";
    this.user = JSON.parse(userStr);
    this.load();
  },
  name: "bookwithuser",
  methods: {
    handleSelectionChange(val) {
      this.forms = val;
    },
    deleteBatch() {
      if (!this.forms.length) {
        ElMessage.warning("请选择数据！");
        return;
      }
      //  一个小优化，直接发送这个数组，而不是一个一个的提交删除
      request.post("housewithuser/deleteRecords", this.forms).then((res) => {
        if (res.code === "0") {
          ElMessage.success("批量删除成功");
          this.load();
        } else {
          ElMessage.error(res.msg);
        }
      });
    },
    load() {
      if (this.user.role == 1) {
        request
          .get("/housewithuser", {
            params: {
              pageNum: this.currentPage,
              pageSize: this.pageSize,
              search1: this.search1,
              search2: this.search2,
              search3: this.search3,
            },
          })
          .then((res) => {
            console.log(res);
            this.tableData = res.data.records;
            this.total = res.data.total;
          });
      } else {
        request
          .get("/housewithuser", {
            params: {
              pageNum: this.currentPage,
              pageSize: this.pageSize,
              search1: this.search1,
              search2: this.search2,
              search3: this.user.id,
            },
          })
          .then((res) => {
            console.log(res);
            this.tableData = res.data.records;
            this.total = res.data.total;
          });
      }
    },
    clear() {
      this.search1 = "";
      this.search2 = "";
      this.search3 = "";
      this.load();
    },
    handleDelete(row) {
      const form3 = JSON.parse(JSON.stringify(row));
      request.post("housewithuser/deleteRecord", form3).then((res) => {
        console.log(res);
        if (res.code == 0) {
          ElMessage.success("删除成功");
        } else ElMessage.error(res.msg);
        this.load();
      });
    },
    save() {
      //ES6语法
      //地址,但是？IP与端口？+请求参数
      // this.form?这是自动保存在form中的，虽然显示时没有使用，但是这个对象中是有它的
      request.post("/housewithuser", this.form).then((res) => {
        console.log(res);
        if (res.code == 0) {
          ElMessage({
            message: "修改信息成功",
            type: "success",
          });
        } else {
          ElMessage.error(res.msg);
        }
        this.load();
        this.dialogVisible2 = false;
      });
    },

    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row));
      this.dialogVisible2 = true;
    },
    handleSizeChange(pageSize) {
      this.pageSize = pageSize;
      this.load();
    },
    handleCurrentChange(pageNum) {
      this.pageNum = pageNum;
      this.load();
    },
  },
  data() {
    return {
      form: {},
      form2: {},
      form3: {},
      dialogVisible: false,
      dialogVisible2: false,
      search1: "",
      search2: "",
      search3: "",
      total: 10,
      currentPage: 1,
      pageSize: 10,
      tableData: [],
      user: {},
      forms: [],
    };
  },
};
</script>
