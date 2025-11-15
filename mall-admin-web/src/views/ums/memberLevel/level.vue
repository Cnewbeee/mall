<template>
  <div class="app-container">
    <el-card class="filter-container" shadow="never">
      <div>
        <i class="el-icon-search"></i>
        <span>筛选搜索</span>
        <el-button
          style="float:right"
          type="primary"
          @click="handleSearchList()"
          size="small">
          查询搜索
        </el-button>
        <el-button
          style="float:right;margin-right: 15px"
          @click="handleResetSearch()"
          size="small">
          重置
        </el-button>
      </div>
      <div style="margin-top: 15px">
        <el-form :inline="true" :model="listQuery" size="small" label-width="140px">
          <el-form-item label="等级名称：">
            <el-input v-model="listQuery.name" class="input-width" placeholder="等级名称" clearable></el-input>
          </el-form-item>
          <el-form-item label="默认状态：">
            <el-select v-model="listQuery.defaultStatus" placeholder="全部" clearable>
              <el-option label="全部" value=""></el-option>
              <el-option label="默认状态" value="1"></el-option>
              <el-option label="非默认状态" value="0"></el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="operate-container" shadow="never">
      <i class="el-icon-tickets"></i>
      <span>会员等级列表</span>
      <el-button size="mini" class="btn-add" @click="handleAdd()" style="margin-left: 20px">添加</el-button>
      <el-button
        size="mini"
        class="btn-delete"
        @click="handleBatchDelete()"
        :disabled="!selectedLevels.length"
        style="margin-left: 10px">
        批量删除
      </el-button>
    </el-card>
    <div class="table-container">
      <el-table
        ref="levelTable"
        :data="list"
        style="width: 100%;"
        v-loading="listLoading"
        border
        @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="60" align="center"></el-table-column>
        <el-table-column label="编号" width="100" align="center">
          <template slot-scope="scope">{{ scope.row.id }}</template>
        </el-table-column>
        <el-table-column label="等级名称" align="center">
          <template slot-scope="scope">{{ scope.row.name }}</template>
        </el-table-column>
        <el-table-column label="成长值" align="center">
          <template slot-scope="scope">{{ scope.row.growthPoint }}</template>
        </el-table-column>
        <el-table-column label="免运费标准" align="center">
          <template slot-scope="scope">{{ scope.row.freeFreightPoint }}</template>
        </el-table-column>
        <el-table-column label="特权" align="center">
          <template slot-scope="scope">
            <div style="display: flex; flex-wrap: wrap; justify-content: center; gap: 5px;">
              <el-tag v-if="scope.row.defaultStatus === 1" type="success" size="mini">
                默认等级
              </el-tag>
              <el-tag v-if="scope.row.priviledgeFreeFreight === 1" size="mini">
                免邮特权
              </el-tag>
              <el-tag v-if="scope.row.priviledgeSignIn === 1" size="mini">
                签到特权
              </el-tag>
              <el-tag v-if="scope.row.priviledgeComment === 1" size="mini">
                评论奖励
              </el-tag>
              <el-tag v-if="scope.row.priviledgePromotion === 1" size="mini">
                专享活动
              </el-tag>
              <el-tag v-if="scope.row.priviledgeMemberPrice === 1" size="mini">
                会员价格
              </el-tag>
              <el-tag v-if="scope.row.priviledgeBirthday === 1" size="mini">
                生日特权
              </el-tag>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="180" align="center">
          <template slot-scope="scope">
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
      <el-pagination
        background
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        layout="total, sizes,prev, pager, next,jumper"
        :current-page.sync="listQuery.pageNum"
        :page-size="listQuery.pageSize"
        :page-sizes="[10, 15, 20]"
        :total="total">
      </el-pagination>
    </div>
    <el-dialog :title="isEdit ? '编辑会员等级' : '添加会员等级'" :visible.sync="dialogVisible" width="40%">
      <el-form :model="memberLevel" ref="memberLevelForm" label-width="150px" size="small">
        <el-form-item label="等级名称：" prop="name">
          <el-input v-model="memberLevel.name" style="width: 250px"></el-input>
        </el-form-item>
        <el-form-item label="成长值：" prop="growthPoint">
          <el-input-number v-model="memberLevel.growthPoint" :min="0" style="width: 250px"></el-input-number>
        </el-form-item>
        <el-form-item label="默认等级：" prop="defaultStatus">
          <el-switch :active-value="1" :inactive-value="0" v-model="memberLevel.defaultStatus">
          </el-switch>
        </el-form-item>
        <el-form-item label="免运费标准：" prop="freeFreightPoint">
          <el-input-number v-model="memberLevel.freeFreightPoint" :min="0" :precision="2"
            style="width: 250px"></el-input-number>
        </el-form-item>
        <el-form-item label="评价成长值：" prop="commentGrowthPoint">
          <el-input-number v-model="memberLevel.commentGrowthPoint" :min="0"
            style="width: 250px"></el-input-number>
        </el-form-item>
        <el-form-item label="特权设置：">
          <el-checkbox v-model="memberLevel.priviledgeFreeFreight" :true-label="1"
            :false-label="0">免邮特权</el-checkbox>
          <el-checkbox v-model="memberLevel.priviledgeSignIn" :true-label="1"
            :false-label="0">签到特权</el-checkbox>
          <el-checkbox v-model="memberLevel.priviledgeComment" :true-label="1"
            :false-label="0">评论奖励</el-checkbox>
          <el-checkbox v-model="memberLevel.priviledgePromotion" :true-label="1"
            :false-label="0">专享活动</el-checkbox>
          <el-checkbox v-model="memberLevel.priviledgeMemberPrice" :true-label="1"
            :false-label="0">会员价格</el-checkbox>
          <el-checkbox v-model="memberLevel.priviledgeBirthday" :true-label="1"
            :false-label="0">生日特权</el-checkbox>
        </el-form-item>
        <el-form-item label="备注：" prop="note">
          <el-input v-model="memberLevel.note" type="textarea" :rows="3" style="width: 250px"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false" size="small">取 消</el-button>
        <el-button type="primary" @click="handleDialogConfirm()" size="small">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { fetchList, createMemberLevel, deleteMemberLevel, updateMemberLevel } from '@/api/memberLevel'
import { formatDate } from '@/utils/date'

const defaultListQuery = {
  pageNum: 1,
  pageSize: 10,
  name: null,
  defaultStatus: null
};
const defaultMemberLevel = {
  id: null,
  name: null,
  growthPoint: 0,
  defaultStatus: 0,
  freeFreightPoint: 0,
  commentGrowthPoint: 0,
  priviledgeFreeFreight: 0,
  priviledgeSignIn: 0,
  priviledgeComment: 0,
  priviledgePromotion: 0,
  priviledgeMemberPrice: 0,
  priviledgeBirthday: 0,
  note: null
};
export default {
  name: 'memberLevelList',
  data() {
    return {
      listQuery: Object.assign({}, defaultListQuery),
      list: null,
      total: null,
      listLoading: false,
      dialogVisible: false,
      memberLevel: Object.assign({}, defaultMemberLevel),
      isEdit: false,
      selectedLevels: [],
      multipleSelection: [],
      memberLevelMap: {},
      memberLevelOptions: []
    }
  },
  created() {
    this.getList();
    this.fetchMemberLevels();
  },
  methods: {
    fetchMemberLevels() {
      // 获取所有会员等级用于下拉框选项
      fetchMemberLevelList().then(response => {
        const levels = response.data.list || [];
        this.memberLevelOptions = levels.map(level => ({
          label: level.name,
          value: level.id
        }));
        levels.forEach(level => {
          this.memberLevelMap[level.id] = level.name;
        });
      });
    },

    handleSelectionChange(val) {
      this.multipleSelection = val;
      this.selectedLevels = val.map(item => item.id);
    },

    handleBatchDelete() {
      if (this.selectedLevels.length === 0) {
        this.$message({
          type: 'warning',
          message: '请至少选择一条记录'
        });
        return;
      }
      this.$confirm('确定要删除选中的会员等级吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteMemberLevel(this.selectedLevels).then(response => {
          this.$message({
            type: 'success',
            message: '删除成功!'
          });
          this.getList();
          this.selectedLevels = [];
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
      this.listQuery.pageSize = val;
      this.getList();
    },

    handleCurrentChange(val) {
      this.listQuery.pageNum = val;
      this.getList();
    },

    handleAdd() {
      this.dialogVisible = true;
      this.isEdit = false;
      this.memberLevel = Object.assign({}, defaultMemberLevel);
    },

    handleDelete(index, row) {
      this.$confirm('确定要删除该会员等级吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteMemberLevel([row.id]).then(response => {
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
      this.memberLevel = Object.assign({}, row);
    },

    handleDialogConfirm() {
      this.$confirm('是否要确认?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        if (this.isEdit) {
          // 更新会员等级信息
          updateMemberLevel(this.memberLevel.id, this.memberLevel).then(response => {
            this.$message({
              message: '修改成功！',
              type: 'success'
            });
            this.dialogVisible = false;
            this.getList();
          }).catch(error => {
            this.$message({
              message: '修改失败',
              type: 'error'
            });
          });
        } else {
          // 创建新的会员等级
          createMemberLevel(this.memberLevel).then(response => {
            this.$message({
              message: '添加成功！',
              type: 'success'
            });
            this.dialogVisible = false;
            this.getList();
          });
        }
      });
    },

    getList() {
      this.listLoading = true;
      fetchList(this.listQuery).then(response => {
        this.listLoading = false;
        this.list = response.data.list;
        this.total = response.data.total;
      });
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
    }
  }
}
</script>

<style scoped>
.input-width {
  width: 203px;
}
</style>
