# Iteration 1 任务分配方案

## 项目概览

**项目名称：** TA招聘系统 (TA Recruitment System)
**Iteration周期：** 2026-03-22 ~ 2026-04-05 (2周)
**团队规模：** 6人
**总工作量：** 13个User Stories，29故事点

## 团队成员技能分布

| 成员编号 | 技能特长 | 主要职责 |
|---------|---------|---------|
| 成员1 | 后端开发 | 数据层 + 申请人模块后端 |
| 成员2 | 后端开发 | 数据层 + 申请人模块后端 |
| 成员3 | 后端开发 | 认证系统 + 组织者模块后端 |
| 成员4 | 全栈开发 | 权限控制 + 组织者模块后端 |
| 成员5 | 全栈开发 | 前端开发 + 申请人视图 |
| 成员6 | 前端开发 | 前端开发 + 组织者视图 + 测试 |

## Iteration 1 User Stories

### 认证与权限（6点）
- **US1**: 用户注册 - Must - 3点
- **US2**: 用户登录 - Must - 2点
- **US3**: 基于角色的访问控制 - Must - 1点

### 申请人基础功能（11点）
- **US4**: 创建申请人档案 - Must - 3点
- **US8**: 上传简历 - Must - 3点
- **US11**: 查看可用职位列表 - Must - 5点
- **US12**: 查看职位详情 - Must - 2点

### 组织者基础功能（9点）
- **US22**: 创建职位发布 - Must - 5点
- **US23**: 设置职位要求 - Must - 2点
- **US24**: 设置工作量和截止日期 - Must - 2点

### 技术基础（3点）
- **US35**: 文本文件数据存储 - Must - 3点

---

## 分工方案：混合模式

### 阶段1：基础架构搭建（Day 1-3）

#### 小组A：数据层团队
**成员：成员1 + 成员2**

**任务：**
- 设计所有实体类（Model层）
  - `User.java` - 用户基础类
  - `Applicant.java` - 申请人信息
  - `Organiser.java` - 组织者信息
  - `Admin.java` - 管理员信息
  - `Job.java` - 职位信息
  - `Application.java` - 申请记录
  - `CV.java` - 简历信息

- 实现文件存储工具类（US35）
  - `FileStorageUtil.java` - JSON/CSV读写工具
  - `DataValidator.java` - 数据验证工具

- 实现DAO接口和实现类
  - `UserDAO.java` + `UserDAOImpl.java`
  - `ApplicantDAO.java` + `ApplicantDAOImpl.java`
  - `JobDAO.java` + `JobDAOImpl.java`
  - `ApplicationDAO.java` + `ApplicationDAOImpl.java`
  - `CVDAO.java` + `CVDAOImpl.java`

**交付物：**
- 完整的Model层代码
- 文件存储工具类
- DAO层接口和实现
- 单元测试

**估算点数：** US35（3点）+ 基础架构工作

---

#### 小组B：认证系统团队
**成员：成员3**

**任务：**
- 实现用户注册功能（US1）
  - `RegisterServlet.java` - 处理注册请求
  - `UserService.java` - 用户业务逻辑
  - 密码哈希处理（使用SHA-256或BCrypt）
  - 重复账号检查
  - 注册成功/失败消息

- 实现用户登录功能（US2）
  - `LoginServlet.java` - 处理登录请求
  - Session管理
  - 登录验证逻辑
  - 根据角色跳转到不同页面

**交付物：**
- RegisterServlet + UserService
- LoginServlet + Session管理
- 单元测试

**估算点数：** US1（3点）+ US2（2点）= 5点

---

#### 小组C：权限控制团队
**成员：成员4**

**任务：**
- 实现基于角色的访问控制（US3）
  - `AuthenticationFilter.java` - 登录检查Filter
  - `AuthorizationFilter.java` - 权限检查Filter
  - `RoleEnum.java` - 角色枚举（APPLICANT, ORGANISER, ADMIN）
  - 配置`web.xml`的Filter映射
  - 实现角色检查工具类`RoleChecker.java`

**交付物：**
- AuthenticationFilter
- AuthorizationFilter
- web.xml配置
- 单元测试

**估算点数：** US3（1点）

---

#### 小组D：前端基础团队
**成员：成员5 + 成员6**

**任务：**
- 将HTML原型转换为JSP模板结构
  - 创建通用组件：`header.jsp`, `footer.jsp`, `navigation.jsp`
  - 配置CSS和静态资源路径
  - 创建错误页面：`404.jsp`, `error.jsp`

- 准备通用页面
  - `login.jsp` - 登录页面（配合US2）
  - `register.jsp` - 注册页面（配合US1）
  - `index.jsp` - 首页

**交付物：**
- JSP模板框架
- 通用组件
- 登录/注册页面

**估算点数：** 基础架构工作

---

### 阶段2：功能模块开发（Day 4-8）

#### 小组A：申请人模块团队
**成员：成员1 + 成员2 + 成员5**

##### 成员1负责：
**US4: 创建申请人档案（3点）**
- `ApplicantService.java` - 申请人业务逻辑
  - `createProfile()` - 创建档案
  - `validateProfile()` - 验证必填字段
- `ApplicantProfileServlet.java` - 处理档案创建请求
- 单元测试

**US8: 上传简历（3点）**
- `CVService.java` - 简历业务逻辑
  - `uploadCV()` - 上传简历
  - `saveFile()` - 保存文件到服务器
  - `linkToProfile()` - 关联到申请人档案
- `CVUploadServlet.java` - 处理文件上传
- 文件存储和路径管理
- 单元测试

**交付物：**
- ApplicantService + ApplicantProfileServlet
- CVService + CVUploadServlet
- 文件上传功能
- 单元测试

**估算点数：** 6点

---

##### 成员2负责：
**US11: 查看可用职位列表（5点）**
- `JobService.java` - 职位业务逻辑
  - `getAvailableJobs()` - 获取开放职位
  - `filterExpiredJobs()` - 过滤过期职位
  - `sortJobs()` - 排序功能（可选）
- `JobListServlet.java` - 处理职位列表请求
- 单元测试

**US12: 查看职位详情（2点）**
- `JobService.java` - 扩展
  - `getJobDetails(jobId)` - 获取职位详情
  - `validateJobExists()` - 验证职位存在
- `JobDetailServlet.java` - 处理职位详情请求
- 单元测试

**交付物：**
- JobService + JobListServlet
- JobDetailServlet
- 单元测试

**估算点数：** 7点

---

##### 成员5负责：
**申请人模块前端（JSP页面）**
- `applicant/create_profile.jsp` - 创建档案页面（US4）
  - 表单：姓名、联系方式、基本信息
  - 前端验证：必填字段检查
  - 成功/错误消息显示

- `applicant/upload_cv.jsp` - 上传简历页面（US8）
  - 文件上传表单
  - 文件类型验证（PDF/DOC）
  - 上传进度提示

- `applicant/job_list.jsp` - 职位列表页面（US11）
  - 职位卡片展示
  - 职位信息：标题、要求、工作量、截止日期
  - 链接到详情页

- `applicant/job_detail.jsp` - 职位详情页面（US12）
  - 完整职位描述
  - 技能要求
  - 工作量和截止日期
  - "申请"按钮（Iteration 2实现）

**交付物：**
- 4个JSP页面
- 前端表单验证JavaScript
- CSS样式调整

**估算点数：** 12点

---

#### 小组B：组织者模块团队
**成员：成员3 + 成员4 + 成员6**

##### 成员3负责：
**US22: 创建职位发布（5点）**
- `JobService.java` - 职位业务逻辑（与成员2协作）
  - `createJob()` - 创建职位
  - `validateJobData()` - 验证职位数据
  - `saveJob()` - 保存职位到文件
- `CreateJobServlet.java` - 处理职位创建请求
- 单元测试

**交付物：**
- JobService.createJob()
- CreateJobServlet
- 单元测试

**估算点数：** 5点

---

##### 成员4负责：
**US23: 设置职位要求（2点）**
- `JobService.java` - 扩展
  - `setRequirements(jobId, requirements)` - 设置技能要求
  - `validateRequirements()` - 验证要求格式
- 集成到CreateJobServlet

**US24: 设置工作量和截止日期（2点）**
- `JobService.java` - 扩展
  - `setWorkloadAndDeadline(jobId, workload, deadline)` - 设置工作量和截止日期
  - `validateDeadline()` - 验证截止日期有效性
- 集成到CreateJobServlet

**交付物：**
- JobService扩展功能
- 集成到CreateJobServlet
- 单元测试

**估算点数：** 4点

---

##### 成员6负责：
**组织者模块前端 + 认证页面**
- `organiser/create_job.jsp` - 创建职位页面（US22, US23, US24）
  - 表单：职位标题、描述
  - 技能要求输入（多行文本或标签）
  - 工作量输入（小时/周）
  - 截止日期选择器
  - 前端验证

- `login.jsp` - 完善登录页面（US2）
  - 用户名/密码表单
  - 错误消息显示
  - 跳转到注册页面链接

- `register.jsp` - 完善注册页面（US1）
  - 注册表单：用户名、密码、角色选择
  - 密码确认
  - 前端验证

**交付物：**
- 3个JSP页面
- 前端表单验证JavaScript
- CSS样式调整

**估算点数：** 9点

---

### 阶段3：集成测试与优化（Day 9-10）

#### 全员任务

**Day 9: 集成测试**
- 成员1+2: 测试申请人模块完整流程
- 成员3+4: 测试组织者模块完整流程
- 成员5+6: 前后端联调，修复UI问题
- 全员: Bug修复

**Day 10: 验收与文档**
- 用户验收测试（UAT）
- 代码审查和重构
- 更新README和技术文档
- Sprint Review准备

---

## 详细任务分配表

| 成员 | User Stories | 主要任务 | 估算点数 | 技术栈 |
|------|-------------|---------|---------|--------|
| **成员1** | US35(部分), US4, US8 | 数据层 + 申请人档案 + 简历上传 | 9点 | Java, Servlet, 文件I/O |
| **成员2** | US35(部分), US11, US12 | 数据层 + 职位列表 + 职位详情 | 10点 | Java, Servlet, JSON |
| **成员3** | US1, US2, US22 | 注册 + 登录 + 创建职位 | 10点 | Java, Servlet, Session |
| **成员4** | US3, US23, US24 | 权限控制 + 职位要求设置 | 5点 | Java, Filter, Servlet |
| **成员5** | US4, US8, US11, US12 (前端) | 申请人模块JSP页面 | 12点 | JSP, HTML, CSS, JS |
| **成员6** | US1, US2, US22-24 (前端) | 认证页面 + 组织者模块JSP | 11点 | JSP, HTML, CSS, JS |

**总计：** 57点（包含基础架构工作）

---

## 工作流程时间表

### Week 1: 基础搭建与核心开发

| 日期 | 阶段 | 主要任务 |
|------|------|---------|
| **Day 1-2** | 基础架构 | - 成员1+2: Model + DAO层<br>- 成员3: 注册/登录Servlet<br>- 成员4: 权限Filter<br>- 成员5+6: JSP模板框架 |
| **Day 3** | 集成与审查 | - 代码审查<br>- 集成测试基础功能<br>- 确认接口规范<br>- 解决依赖问题 |
| **Day 4-6** | 功能开发 | - 成员1: US4, US8后端<br>- 成员2: US11, US12后端<br>- 成员3: US22后端<br>- 成员4: US23, US24后端<br>- 成员5: 申请人JSP<br>- 成员6: 组织者JSP |
| **Day 7** | 功能完善 | - 完成剩余功能<br>- 前后端联调<br>- 修复集成问题 |

### Week 2: 测试与交付

| 日期 | 阶段 | 主要任务 |
|------|------|---------|
| **Day 8** | 联调测试 | - 前后端完整联调<br>- 功能测试<br>- Bug修复 |
| **Day 9** | 集成测试 | - 完整流程测试<br>- 性能测试<br>- 安全测试<br>- Bug修复 |
| **Day 10** | 验收交付 | - 用户验收测试<br>- 文档完善<br>- Sprint Review<br>- Sprint Retrospective |

---

## 协作机制

### 每日站会（Daily Standup）
**时间：** 每天上午9:30，15分钟
**内容：**
1. 昨天完成了什么？
2. 今天计划做什么？
3. 遇到什么阻碍？

**站会规则：**
- 准时开始，严格控制时间
- 每人发言不超过2分钟
- 只讨论进度和阻碍，技术细节会后讨论

---

### 代码审查（Code Review）
**规则：**
- 每个Pull Request至少需要1人审查通过才能合并
- 小组内成员互相审查
- 关键模块（认证、权限、数据层）需要2人审查
- 审查重点：代码规范、安全性、性能、测试覆盖

**审查分工：**
- 成员1审查成员2的代码，成员2审查成员1的代码
- 成员3审查成员4的代码，成员4审查成员3的代码
- 成员5审查成员6的代码，成员6审查成员5的代码

---

### 分支策略（Git Workflow）
```
main (受保护分支)
  └── dev/iteration1 (开发分支)
       ├── dev/wzx (成员1个人分支)
       ├── dev/cmy (成员2个人分支)
       ├── dev/dmd (成员3个人分支)
       ├── dev/qh (成员4个人分支)
       ├── dev/gc (成员5个人分支)
       └── dev/tyx (成员6个人分支)
```

**工作流程：**
1. 从`dev/iteration1`创建个人分支
2. 在个人分支上开发功能
3. 完成后提交PR到`dev/iteration1`
4. 代码审查通过后合并
5. Iteration结束后，将`dev/iteration1`合并到`main`

**提交规范：**
```
feat: 添加用户注册功能 (US1)
fix: 修复登录验证bug
refactor: 重构DAO层代码
test: 添加JobService单元测试
docs: 更新API文档
```

---

### 沟通渠道
- **紧急问题：** 电话/即时通讯
- **技术讨论：** GitHub Issues / 团队群聊
- **文档共享：** GitHub Wiki / Google Docs
- **进度跟踪：** GitHub Project Board

---

## 技术规范

### 后端开发规范
**包结构：**
```
com.bupt.tarecruit
├── model/          # 实体类
├── dao/            # 数据访问层
├── service/        # 业务逻辑层
├── controller/     # Servlet控制器
├── filter/         # 过滤器
└── util/           # 工具类
```

**命名规范：**
- 类名：大驼峰（PascalCase）- `UserService`
- 方法名：小驼峰（camelCase）- `createProfile()`
- 常量：全大写+下划线 - `MAX_FILE_SIZE`
- 包名：全小写 - `com.bupt.tarecruit.service`

**代码规范：**
- 使用Java 17特性
- 遵循阿里巴巴Java开发手册
- 每个public方法必须有Javadoc注释
- 异常处理：不要吞掉异常，记录日志
- 单元测试覆盖率 > 70%

---

### 前端开发规范
**目录结构：**
```
webapp/
├── jsp/
│   ├── applicant/      # 申请人页面
│   ├── organiser/      # 组织者页面
│   ├── admin/          # 管理员页面
│   └── common/         # 通用组件
├── css/
├── js/
└── images/
```

**命名规范：**
- 文件名：小写+下划线 - `create_profile.jsp`
- CSS类名：小写+连字符 - `.job-card`
- JavaScript变量：小驼峰 - `jobList`

**代码规范：**
- 使用JSTL标签，避免JSP脚本
- 表单必须有前端验证
- 响应式设计，支持移动端
- 无障碍访问（ARIA标签）

---

### 数据存储规范（US35）
**文件格式：**
- 用户数据：`data/users.json`
- 申请人数据：`data/applicants.json`
- 职位数据：`data/jobs.json`
- 申请记录：`data/applications.json`
- 简历文件：`data/cv/{applicantId}/`

**JSON格式示例：**
```json
{
  "users": [
    {
      "id": "U001",
      "username": "john_doe",
      "passwordHash": "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8",
      "role": "APPLICANT",
      "createdAt": "2026-03-22T10:30:00Z"
    }
  ]
}
```

**文件操作规范：**
- 使用Gson库进行JSON序列化/反序列化
- 文件读写必须加锁，防止并发问题
- 定期备份数据文件
- 错误处理：文件不存在时自动创建

---

## 风险管理

### 风险识别与应对

| 风险 | 可能性 | 影响 | 应对措施 |
|------|-------|------|---------|
| **US11工作量超出预期** | 中 | 高 | - 成员4完成US3后支援成员2<br>- 简化功能，延后分页到Iteration 2 |
| **文件并发读写冲突** | 高 | 高 | - 实现文件锁机制<br>- 使用synchronized关键字<br>- 考虑使用队列处理写操作 |
| **成员请假或生病** | 低 | 中 | - 小组内互相支援<br>- 调整任务优先级<br>- 延后非Must级别功能 |
| **前后端接口不匹配** | 中 | 中 | - Day 3确认接口规范<br>- 使用Postman测试API<br>- 编写接口文档 |
| **代码合并冲突** | 高 | 低 | - 频繁从dev/iteration1拉取更新<br>- 小步提交，避免大改动<br>- 及时沟通修改范围 |

---

### 质量保证

**测试策略：**
1. **单元测试**（每个成员负责自己的代码）
   - 使用JUnit 5
   - 覆盖率 > 70%
   - 重点测试业务逻辑和边界条件

2. **集成测试**（Day 8-9）
   - 测试完整用户流程
   - 测试前后端集成
   - 测试文件读写

3. **用户验收测试**（Day 10）
   - 按照Acceptance Criteria验证
   - 邀请Product Owner参与
   - 记录反馈和改进点

**代码质量检查：**
- 使用SonarLint进行静态代码分析
- 检查代码规范（Checkstyle）
- 安全漏洞扫描

---

## 交付清单

### Iteration 1 交付物

**代码：**
- [ ] 完整的Model层（7个实体类）
- [ ] 完整的DAO层（5个DAO接口+实现）
- [ ] Service层（UserService, ApplicantService, JobService, CVService）
- [ ] Controller层（8个Servlet）
- [ ] Filter层（AuthenticationFilter, AuthorizationFilter）
- [ ] 工具类（FileStorageUtil, DataValidator, RoleChecker）
- [ ] JSP页面（7个页面）

**测试：**
- [ ] 单元测试（覆盖率 > 70%）
- [ ] 集成测试用例
- [ ] 测试报告

**文档：**
- [ ] API接口文档
- [ ] 数据模型文档
- [ ] 部署指南
- [ ] 用户手册（初版）
- [ ] Sprint Review报告
- [ ] Sprint Retrospective总结

**数据：**
- [ ] 测试数据（至少5个用户，10个职位）
- [ ] 数据文件结构

---

## 成功标准

### Definition of Done (DoD)

一个User Story被认为"完成"需要满足：
1. ✅ 代码已编写并通过编译
2. ✅ 单元测试已编写并通过
3. ✅ 代码已提交并通过Code Review
4. ✅ 功能已集成到dev/iteration1分支
5. ✅ 所有Acceptance Criteria已验证通过
6. ✅ 无已知的Critical/High级别bug
7. ✅ 相关文档已更新

### Iteration 1 成功标准

- ✅ 13个User Story全部完成（满足DoD）
- ✅ 用户可以注册、登录系统
- ✅ 申请人可以创建档案、上传简历、浏览职位
- ✅ 组织者可以创建职位发布
- ✅ 基于角色的访问控制正常工作
- ✅ 数据正确存储在JSON文件中
- ✅ 代码覆盖率 > 70%
- ✅ 无Critical级别bug

---

## 附录

### 联系方式

| 成员 | GitHub账号 | 分支 | 邮箱 |
|------|-----------|------|------|
| 成员1 | @wzx | dev/wzx | wzx@example.com |
| 成员2 | @cmy | dev/cmy | cmy@example.com |
| 成员3 | @dmd | dev/dmd | dmd@example.com |
| 成员4 | @qh | dev/qh | qh@example.com |
| 成员5 | @gc | dev/gc | gc@example.com |
| 成员6 | @tyx | dev/tyx | tyx@example.com |

### 会议安排

| 会议 | 时间 | 参与者 | 目的 |
|------|------|--------|------|
| Daily Standup | 每天 9:30 | 全员 | 同步进度，识别阻碍 |
| Code Review | 每天 16:00 | 相关成员 | 代码审查 |
| Sprint Planning | Day 1 上午 | 全员 | 任务分配，目标确认 |
| Sprint Review | Day 10 下午 | 全员 + PO | 演示成果，收集反馈 |
| Sprint Retrospective | Day 10 晚上 | 全员 | 总结经验，改进流程 |

---

## 文档版本

- **版本：** v1.0
- **创建日期：** 2026-03-18
- **最后更新：** 2026-03-18
- **创建者：** 项目团队
- **审核者：** Product Owner

---

**祝Iteration 1开发顺利！🚀**
