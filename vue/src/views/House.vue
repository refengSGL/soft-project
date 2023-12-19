<template>
  <div class="home" style="padding: 10px">
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
        <el-form-item label="房主">
          <el-input v-model="search3" placeholder="请输入房主名" clearable>
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
          >
            <svg-icon iconClass="search" />查询</el-button
          >
        </el-form-item>
        <el-form-item>
          <el-button size="mini" type="danger" @click="clear">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <!-- 按钮-->
    <div style="margin: 10px 0">
      <el-button type="primary" @click="add" v-if="user.role == 1"
        >上架</el-button
      >
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
      <el-table-column prop="number" label="房源编号" sortable />
      <el-table-column prop="name" label="房源名称" />
      <el-table-column prop="price" label="价格" sortable />
      <el-table-column prop="author" label="房主" />
      <el-table-column prop="publisher" label="发布者" />
      <el-table-column prop="createTime" label="发布时间" sortable />
      <el-table-column prop="status" label="状态">
        <template v-slot="scope">
          <el-tag v-if="scope.row.status == 0" type="success">已售出</el-tag>
          <el-tag v-else type="warning">未售出</el-tag>
        </template>
      </el-table-column>
      console.log(scope.row.number);
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
            @confirm="handleDelete(scope.row.id)"
            v-if="user.role == 1"
          >
            <template #reference>
              <el-button type="danger" size="mini">删除</el-button>
            </template>
          </el-popconfirm>

          <el-button
            size="mini"
            @click="
              handlelend(
                scope.row.id,
                scope.row.number,
                scope.row.name,
                scope.row.borrownum
              )
            "
            v-if="user.role == 2"
            :disabled="scope.row.status == 0"
            >购买
          </el-button>

          <el-popconfirm
            title="确认取消?"
            @confirm="
              handlereturn(scope.row.id, scope.row.number, scope.row.borrownum)
            "
            v-if="user.role == 2"
            :disabled="scope.row.status == 1"
          >
            <template #reference>
              <el-button
                type="danger"
                size="mini"
                :disabled="
                  this.isbnArray.indexOf(scope.row.isbn) == -1 ||
                  scope.row.status == 1
                "
                >取消</el-button
              >
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

      <el-dialog v-model="dialogVisible" title="上架房源" width="30%">
        <el-form :model="form" label-width="120px">
          <el-form-item label="房源编号">
            <el-input style="width: 80%" v-model="form.number"></el-input>
          </el-form-item>
          <el-form-item label="房源名称">
            <el-input style="width: 80%" v-model="form.name"></el-input>
          </el-form-item>
          <el-form-item label="价格">
            <el-input style="width: 80%" v-model="form.price"></el-input>
          </el-form-item>
          <el-form-item label="房主">
            <el-input style="width: 80%" v-model="form.author"></el-input>
          </el-form-item>
          <el-form-item label="发布者">
            <el-input style="width: 80%" v-model="form.publisher"></el-input>
          </el-form-item>
          <el-form-item label="发布时间">
            <div>
              <el-date-picker
                value-format="YYYY-MM-DD"
                type="date"
                style="width: 80%"
                clearable
                v-model="form.createTime"
              ></el-date-picker>
            </div>
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="dialogVisible = false">取 消</el-button>
            <el-button type="primary" @click="save">确 定</el-button>
          </span>
        </template>
      </el-dialog>

      <el-dialog v-model="dialogVisible2" title="修改房源信息" width="30%">
        <el-form :model="form" label-width="120px">
          <el-form-item label="房源编号">
            <el-input style="width: 80%" v-model="form.number"></el-input>
          </el-form-item>
          <el-form-item label="房源名称">
            <el-input style="width: 80%" v-model="form.name"></el-input>
          </el-form-item>
          <el-form-item label="价格">
            <el-input style="width: 80%" v-model="form.price"></el-input>
          </el-form-item>
          <el-form-item label="房主">
            <el-input style="width: 80%" v-model="form.author"></el-input>
          </el-form-item>
          <el-form-item label="发布者">
            <el-input style="width: 80%" v-model="form.publisher"></el-input>
          </el-form-item>
          <el-form-item label="发布时间">
            <div>
              <el-date-picker
                value-format="YYYY-MM-DD"
                type="date"
                style="width: 80%"
                clearable
                v-model="form.createTime"
              ></el-date-picker>
            </div>
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="dialogVisible = false">取 消</el-button>
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
  name: "Book",
  methods: {
    // (this.isbnArray.indexOf(scope.row.isbn)) == -1
    handleSelectionChange(val) {
      this.ids = val.map((v) => v.id);
    },
    deleteBatch() {
      if (!this.ids.length) {
        ElMessage.warning("请选择数据！");
        return;
      }
      //  一个小优化，直接发送这个数组，而不是一个一个的提交删除
      request.post("/house/deleteBatch", this.ids).then((res) => {
        if (res.code === "0") {
          ElMessage.success("批量删除成功");
          this.load();
        } else {
          ElMessage.error(res.msg);
        }
      });
    },
    load() {
      this.numOfOutDataBook = 0;
      this.outDateBook = [];
      request
        .get("/house", {
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
      //
      if (this.user.role == 2) {
        request
          .get("/housewithuser", {
            params: {
              pageNum: "1",
              pageSize: this.total,
              search1: "",
              search2: "",
              search3: this.user.id,
            },
          })
          .then((res) => {
            console.log(res);
            this.bookData = res.data.records;
            this.number = this.bookData.length;
            var nowDate = new Date();
            for (let i = 0; i < this.number; i++) {
              this.isbnArray[i] = this.bookData[i].isbn;
              let dDate = new Date(this.bookData[i].deadtime);
              if (dDate < nowDate) {
                this.outDateBook[this.numOfOutDataBook] = {
                  isbn: this.bookData[i].isbn,
                  bookName: this.bookData[i].bookName,
                  deadtime: this.bookData[i].deadtime,
                  lendtime: this.bookData[i].lendtime,
                };
                this.numOfOutDataBook = this.numOfOutDataBook + 1;
              }
            }
            console.log("in load():" + this.numOfOutDataBook);
          });
      }
      //
    },
    clear() {
      this.search1 = "";
      this.search2 = "";
      this.search3 = "";
      this.load();
    },
    handleDelete(id) {
      request.delete("house/" + id).then((res) => {
        console.log(res);
        if (res.code == 0) {
          ElMessage.success("删除成功");
        } else ElMessage.error(res.msg);
        this.load();
      });
    },
    handlereturn(id, isbn, bn) {
      this.form.status = "1";
      this.form.id = id;
      request.put("/house", this.form).then((res) => {
        console.log(res);
        if (res.code == 0) {
          ElMessage({
            message: "取消成功",
            type: "success",
          });
        } else {
          ElMessage.error(res.msg);
        }
        //
        this.form3.isbn = isbn;
        this.form3.readerId = this.user.id;
        let endDate = moment(new Date()).format("yyyy-MM-DD HH:mm:ss");
        this.form3.returnTime = endDate;
        this.form3.status = "1";
        console.log(bn);
        this.form3.borrownum = bn;
        request.put("/LendRecord1/", this.form3).then((res) => {
          console.log(res);
          let form3 = {};
          form3.isbn = isbn;
          form3.bookName = name;
          form3.nickName = this.user.username;
          form3.id = this.user.id;
          form3.lendtime = endDate;
          form3.deadtime = endDate;
          form3.prolong = 1;
          request.post("/housewithuser/deleteRecord", form3).then((res) => {
            console.log(res);
            this.load();
          });
        });
        //
      });
      this.form3.isbn = isbn;
      this.form3.readerId = this.user.id;
      let endDate = moment(new Date()).format("yyyy-MM-DD HH:mm:ss");
      this.form3.returnTime = endDate;
      this.form3.status = "1";
      console.log(bn);
      this.form3.borrownum = bn;
      request.put("/LendRecord1/", this.form3).then((res) => {
        console.log(res);
      });
      let form3 = {};
      form3.isbn = isbn;
      form3.bookName = name;
      form3.nickName = this.user.username;
      form3.id = this.user.id;
      form3.lendtime = endDate;
      form3.deadtime = endDate;
      form3.prolong = 1;
      request.post("/housewithuser/deleteRecord", form3).then((res) => {
        console.log(res);
        this.load();
      });
    },
    handlelend(id, isbn, name, bn) {
      this.form.status = "0";
      this.form.id = id;
      this.form.borrownum = bn + 1;
      console.log(bn);
      request.put("/house", this.form).then((res) => {
        console.log(res);
        if (res.code == 0) {
          ElMessage({
            message: "购买成功",
            type: "success",
          });
        } else {
          ElMessage.error(res.msg);
        }
      });

      this.form2.status = "0";
      this.form2.isbn = isbn;
      this.form2.bookname = name;
      this.form2.readerId = this.user.id;
      this.form2.borrownum = bn + 1;
      console.log(this.form2.borrownum);
      console.log(this.user);
      let startDate = moment(new Date()).format("yyyy-MM-DD HH:mm:ss");
      this.form2.lendTime = startDate;
      console.log(this.user);
      request.post("/LendRecord", this.form2).then((res) => {
        console.log(res);
        this.load();
      });
      let form3 = {};
      form3.isbn = isbn;
      form3.bookName = name;
      form3.nickName = this.user.username;
      form3.id = this.user.id;
      form3.lendtime = startDate;
      let nowDate = new Date(startDate);
      nowDate.setDate(nowDate.getDate() + 30);
      form3.deadtime = moment(nowDate).format("yyyy-MM-DD HH:mm:ss");
      form3.prolong = 1;
      request.post("/housewithuser/insertNew", form3).then((res) => {
        console.log(res);
        this.load();
      });
    },
    add() {
      this.dialogVisible = true;
      this.form = {};
    },
    save() {
      //ES6语法
      //地址,但是？IP与端口？+请求参数
      // this.form?这是自动保存在form中的，虽然显示时没有使用，但是这个对象中是有它的
      if (this.form.id) {
        request.put("/house", this.form).then((res) => {
          console.log(res);
          if (res.code == 0) {
            ElMessage({
              message: "修改房源信息成功",
              type: "success",
            });
          } else {
            ElMessage.error(res.msg);
          }

          this.load();
          this.dialogVisible2 = false;
        });
      } else {
        this.form.borrownum = 0;
        this.form.status = 0;
        request.post("/house", this.form).then((res) => {
          console.log(res);
          if (res.code == 0) {
            ElMessage.success("上架房源成功");
          } else {
            ElMessage.error(res.msg);
          }
          this.load();
          this.dialogVisible = false;
        });
      }
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
    toLook() {
      this.dialogVisible3 = true;
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
      number: 0,
      bookData: [],
      isbnArray: [],
      outDateBook: [],
      numOfOutDataBook: 0,
      dialogVisible3: true,
    };
  },
};
</script>
