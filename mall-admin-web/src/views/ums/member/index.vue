<template>
  <div class="app-container">
    <el-card class="filter-container" shadow="never">
      <div>
        <i class="el-icon-search"></i>
        <span>筛选搜索</span>
        <el-button style="float:right" type="primary" @click="handleSearchList()" size="small">
          查询搜索
        </el-button>
        <el-button style="float:right;margin-right: 15px" @click="handleResetSearch()" size="small">
          重置
        </el-button>
      </div>
      <div style="margin-top: 15px">
        <el-form :inline="true" :model="listQuery" size="small" label-width="140px">
          <el-form-item label="用户名：">
            <el-input v-model="listQuery.username" class="input-width" placeholder="用户名" clearable></el-input>
          </el-form-item>
          <el-form-item label="昵称：">
            <el-input v-model="listQuery.nickname" class="input-width" placeholder="昵称" clearable></el-input>
          </el-form-item>
          <el-form-item label="手机号：">
            <el-input v-model="listQuery.phone" class="input-width" placeholder="手机号" clearable></el-input>
          </el-form-item>
          <el-form-item label="状态：">
            <el-select v-model="listQuery.status" placeholder="全部" clearable>
              <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="operate-container" shadow="never">
      <i class="el-icon-tickets"></i>
      <span>会员列表</span>
      <el-button size="mini" class="btn-add" @click="handleAdd()" style="margin-left: 20px">添加</el-button>
      <el-button size="mini" class="btn-delete" @click="handleBatchDelete()" :disabled="!selectedMembers.length"
        style="margin-left: 10px">
        批量删除
      </el-button>
    </el-card>
    <div class="table-container">
      <el-table ref="memberTable" :data="list" style="width: 100%;" v-loading="listLoading" border
        @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="60" align="center"></el-table-column>
        <el-table-column label="编号" width="100" align="center">
          <template slot-scope="scope">{{ scope.row.id }}</template>
        </el-table-column>
        <el-table-column label="用户名" align="center">
          <template slot-scope="scope">{{ scope.row.username }}</template>
        </el-table-column>
        <el-table-column label="昵称" align="center">
          <template slot-scope="scope">{{ scope.row.nickname }}</template>
        </el-table-column>
        <el-table-column label="性别" align="center">
          <template slot-scope="scope">
            <span v-if="scope.row.gender === 1">男</span>
            <span v-else-if="scope.row.gender === 2">女</span>
            <span v-else>未知</span>
          </template>
        </el-table-column>
        <el-table-column label="生日" align="center">
          <template slot-scope="scope">{{ scope.row.birthday ? formatDate(scope.row.birthday) : '未填写' }}</template>
        </el-table-column>
        <el-table-column label="会员等级" align="center">
          <template slot-scope="scope">
            <el-tag :type="getLevelTagType(scope.row.memberLevelId)" size="small" style="margin: 2px;">
              {{ memberLevelMap[scope.row.memberLevelId] || '无' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="手机号" align="center">
          <template slot-scope="scope">{{ scope.row.phone }}</template>
        </el-table-column>
        <el-table-column label="注册时间" width="160" align="center">
          <template slot-scope="scope">{{ formatDateTime(scope.row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="会员积分" align="center">
          <template slot-scope="scope">{{ scope.row.integration }}</template>
        </el-table-column>
        <el-table-column label="状态" width="140" align="center">
          <template slot-scope="scope">
            <el-switch @change="handleStatusChange(scope.$index, scope.row)" :active-value="1" :inactive-value="0"
              v-model="scope.row.status">
            </el-switch>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center">
          <template slot-scope="scope">
            <el-button size="mini" type="text"  @click="getIntegration(scope)">
              历史积分
            </el-button>
            <el-button size="mini" type="text" @click="handleUpdate(scope.$index, scope.row)">
              编辑
            </el-button>
            <el-button size="mini" type="text" @click="handleDelete(scope.$index, scope.row)">删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="pagination-container">
      <el-pagination background @size-change="handleSizeChange" @current-change="handleCurrentChange"
        layout="total, sizes,prev, pager, next,jumper" :current-page.sync="listQuery.pageNum"
        :page-size="listQuery.pageSize" :page-sizes="[10, 15, 20]" :total="total">
      </el-pagination>
    </div>
    <el-dialog :title="isEdit ? '编辑会员' : '添加会员'" :visible.sync="dialogVisible" width="40%">
      <el-form :model="member" ref="memberForm" label-width="120px" size="small">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名：" prop="username">
              <el-input v-model="member.username" style="width: 90%"></el-input>
            </el-form-item>
            <el-form-item label="密码：" prop="password">
              <el-input v-model="member.password" type="password" style="width: 90%"></el-input>
            </el-form-item>
            <el-form-item label="昵称：" prop="nickname">
              <el-input v-model="member.nickname" style="width: 90%"></el-input>
            </el-form-item>
            <el-form-item label="会员等级：" prop="memberLevelId">
              <el-select v-model="member.memberLevelId" placeholder="请选择会员等级" style="width: 90%" clearable>
                <el-option v-for="item in memberLevelOptions" :key="item.value" :label="item.label" :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="手机号：" prop="phone">
              <el-input v-model="member.phone" style="width: 90%"></el-input>
            </el-form-item>
            <el-form-item label="状态：" prop="status">
              <el-radio-group v-model="member.status">
                <el-radio :label="1">启用</el-radio>
                <el-radio :label="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="头像：" prop="icon">
              <el-input v-model="member.icon" style="width: 90%"></el-input>
            </el-form-item>
            <el-form-item label="性别：" prop="gender">
              <el-radio-group v-model="member.gender">
                <el-radio :label="0">未知</el-radio>
                <el-radio :label="1">男</el-radio>
                <el-radio :label="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="生日：" prop="birthday">
              <el-date-picker v-model="member.birthday" type="date" placeholder="选择日期" style="width: 90%"></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="注册时间：" prop="createTime">
              <el-date-picker v-model="member.createTime" type="datetime" placeholder="选择注册时间" style="width: 90%"></el-date-picker>
            </el-form-item>
            <el-form-item label="城市：" prop="city">
              <el-input v-model="member.city" style="width: 90%"></el-input>
            </el-form-item>
            <el-form-item label="职业：" prop="job">
              <el-input v-model="member.job" style="width: 90%"></el-input>
            </el-form-item>
            <el-form-item label="个性签名：" prop="personalizedSignature">
              <el-input v-model="member.personalizedSignature" type="textarea" :rows="3" style="width: 90%"></el-input>
            </el-form-item>
            <el-form-item label="来源类型：" prop="sourceType">
              <el-input v-model="member.sourceType" style="width: 90%"></el-input>
            </el-form-item>
            <el-form-item label="当前积分：" prop="integration">
              <el-input v-model="member.integration" style="width: 90%"></el-input>
            </el-form-item>
            <el-form-item label="成长值：" prop="growth">
              <el-input v-model="member.growth" style="width: 90%"></el-input>
            </el-form-item>
            <el-form-item label="幸运值：" prop="luckeyCount">
              <el-input v-model="member.luckeyCount" style="width: 90%"></el-input>
            </el-form-item>
            <el-form-item label="历史积分：" prop="historyIntegration">
              <el-input v-model="member.historyIntegration" style="width: 90%"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false" size="small">取 消</el-button>
        <el-button type="primary" @click="handleDialogConfirm()" size="small">确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog title="积分变化历史" :visible.sync="historyDialogVisible" width="50%">
      <el-table :data="integrationHistory" style="width: 100%">
        <el-table-column prop="createTime" label="时间" width="180">
          <template slot-scope="scope">{{ formatDateTime(scope.row.createTime) }}</template>
        </el-table-column>
        <el-table-column prop="changeType" label="类型">
          <template slot-scope="scope">
            <span v-if="scope.row.changeType === 0">增加</span>
            <span v-else>减少</span>
          </template>
        </el-table-column>
        <el-table-column prop="changeCount" label="积分变化"></el-table-column>
        <el-table-column prop="operateMan" label="操作人员"></el-table-column>
        <el-table-column prop="operateNote" label="备注"></el-table-column>
        <el-table-column prop="sourceType" label="来源" >
          <template slot-scope="scope">
            <span v-if="scope.row.sourceType === 0">购物</span>
            <span v-else>管理员修改</span>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        background
        layout="total, sizes, prev, pager, next, jumper"
        :current-page.sync="integrationQuery.pageNum"
        :page-size.sync="integrationQuery.pageSize"
        :total="integrationQuery.total"
        @size-change="handleIntegrationSizeChange"
        @current-change="handleIntegrationPageChange"
      />
      <span slot="footer" class="dialog-footer">
        <el-button @click="historyDialogVisible = false">关闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import { fetchList, createMember, updateMember, deleteMember, updateStatus, fetchIntegrationHistory } from '@/api/member'
import { formatDate } from '@/utils/date'
import { fetchMemberLevelList } from '@/api/memberLevel'

const defaultListQuery = {
  pageNum: 1,
  pageSize: 10,
  username: null,
  nickname: null,
  phone: null,
  status: null
};
const defaultMember = {
  id: null,
  username: null,
  password: null,
  nickname: null,
  phone: null,
  status: 1,
  icon: null,
  gender: 0,
  birthday: null,
  city: null,
  job: null,
  personalizedSignature: null,
  memberLevelId: null
};
export default {
  name: 'memberList',
  data() {
    return {
      listQuery: Object.assign({}, defaultListQuery),
      list: null,
      total: null,
      listLoading: false,
      dialogVisible: false,
      member: Object.assign({}, defaultMember),
      isEdit: false,
      statusOptions: [
        {
          label: '启用',
          value: 1
        },
        {
          label: '禁用',
          value: 0
        }
      ],
      selectedMembers: [], // 新增：存储选中的会员id
      multipleSelection: [], // 新增：存储选中的行
      memberLevelMap: {},// 新增：存储会员等级映射
      memberLevelOptions: [],
      integrationHistory: [], // 新增：存储积分变化历史记录
      historyDialogVisible: false, // 新增：控制积分历史弹窗的显示
      integrationQuery: {
        pageNum: 1,
        pageSize: 10,
        total: 0
      }
    }
  },
  created() {
    this.getList();
    this.fetchMemberLevels();
  },
  // ...existing code...
  methods: {
    formatDateTime(time) {
      if (!time) return 'N/A';
      // 兼容后端返回的 ISO8601 字符串
      const date = new Date(time);
      if (isNaN(date.getTime())) return 'N/A';
      const year = date.getFullYear();
      const month = (date.getMonth() + 1).toString().padStart(2, '0');
      const day = date.getDate().toString().padStart(2, '0');
      const hours = date.getHours().toString().padStart(2, '0');
      const minutes = date.getMinutes().toString().padStart(2, '0');
      const seconds = date.getSeconds().toString().padStart(2, '0');
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
    },
    formatDate(time) {
      if (!time) return 'N/A';
      // 兼容后端返回的 ISO8601 字符串
      const date = new Date(time);
      if (isNaN(date.getTime())) return 'N/A';
      const year = date.getFullYear();
      const month = (date.getMonth() + 1).toString().padStart(2, '0');
      const day = date.getDate().toString().padStart(2, '0');
      return `${year}-${month}-${day}`;
    },
    getLevelTagType(levelId) {
      // 根据等级ID返回不同的样式类型
      switch (levelId) {
        case 1: return '';        // 普通会员
        case 2: return 'info';    // 白银会员
        case 3: return 'success'; // 黄金会员
        case 4: return 'warning'; // 白金会员
        case 5: return 'danger';  // 钻石会员
        default: return '';      // 其他情况
      }
    },
    fetchMemberLevels() {
      fetchMemberLevelList().then(response => {
        const levels = response.data.list || [];
        this.memberLevelMap = {};
        this.memberLevelOptions = levels.map(level => ({
          label: level.name,
          value: level.id
        }));
        levels.forEach(level => {
          this.memberLevelMap[level.id] = level.name;
        });
      });
    },

    // 新增：处理选中行变化
    handleSelectionChange(val) {
      this.multipleSelection = val;
      this.selectedMembers = val.map(item => item.id);
    },

    // 新增：批量删除方法
    handleBatchDelete() {
      if (this.selectedMembers.length === 0) {
        this.$message({
          type: 'warning',
          message: '请至少选择一条记录'
        });
        return;
      }
      this.$confirm('确定要删除选中的会员吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteMember(this.selectedMembers).then(response => {
          this.$message({
            type: 'success',
            message: '删除成功!'
          });
          this.getList();
          this.selectedMembers = [];
        });
      });
    },
    handleResetSearch() {
      this.listQuery = Object.assign({}, defaultListQuery);
      this.getList();
    },
    handleSearchList() {
      this.listQuery.pageNum = 1;
      this.getList();
    },
    handleSizeChange(val) {
      this.listQuery.pageNum = 1;
      this.listQuery.pageSize = val;
      this.getList();
    },
    handleCurrentChange(val) {
      this.listQuery.pageNum = val;
      this.getList();
    },
    getIntegration(scope) {
      const { pageNum, pageSize } = this.integrationQuery;
      fetchIntegrationHistory(scope.row.id, { pageNum, pageSize }).then(response => {
        this.integrationHistory = response.data.list;
        this.integrationQuery.total = response.data.total;
        this.historyDialogVisible = true;
      });
    },
    handleIntegrationPageChange(pageNum) {
      this.integrationQuery.pageNum = pageNum;
      if (this.integrationHistory.length > 0) {
        this.getIntegration({ row: { id: this.integrationHistory[0].memberId } });
      }
    },
    handleIntegrationSizeChange(pageSize) {
      this.integrationQuery.pageSize = pageSize;
      this.integrationQuery.pageNum = 1;
      if (this.integrationHistory.length > 0) {
        this.getIntegration({ row: { id: this.integrationHistory[0].memberId } });
      }
    },
    handleAdd() {
      this.dialogVisible = true;
      this.isEdit = false;
      this.member = Object.assign({}, defaultMember);
    },
    handleStatusChange(index, row) {
      this.$confirm('是否要修改该状态?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        updateStatus(row.id, row).then(response => {
          this.$message({
            type: 'success',
            message: '修改成功!'
          });
        });
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '取消修改'
        });
        // 如果取消修改，恢复原始状态
        row.status = row.status === 1 ? 0 : 1;
      });
    },
    handleDelete(index, row) {
      this.$confirm('确定要删除该会员吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteMember([row.id]).then(response => {
          this.$message({
            type: 'success',
            message: '删除成功!'
          });
          this.getList();
        });
      });
    },
    handleUpdate(index, row) {
      this.dialogVisible = true;
      this.isEdit = true;
      this.member = Object.assign({}, row);
    },
    handleDialogConfirm() {
      this.$confirm('是否要确认?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        if (this.isEdit) {
          updateMember(this.member.id, this.member).then(response => {
            this.$message({
              message: '修改成功！',
              type: 'success'
            });
            this.dialogVisible = false;
            this.getList();
          })
        } else {
          createMember(this.member).then(response => {
            this.$message({
              message: '添加成功！',
              type: 'success'
            });
            this.dialogVisible = false;
            this.getList();
          })
        }
      })
    },
    getList() {
      this.listLoading = true;
      fetchList(this.listQuery).then(response => {
        this.listLoading = false;
        this.list = response.data.list;
        this.total = response.data.total;
      });
    }
  }
}
</script>
<style></style>
