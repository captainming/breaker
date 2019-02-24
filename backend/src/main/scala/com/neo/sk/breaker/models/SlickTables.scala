package com.neo.sk.breaker.models

// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object SlickTables extends {
  val profile = slick.jdbc.PostgresProfile
} with SlickTables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait SlickTables {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Array(tAbility.schema, tBaseDescription.schema, tDolists.schema, tGpuApply.schema, tGpuManager.schema, tGpuUser.schema, tResource.schema, tResourceAttach.schema, tResourceType.schema, tUser.schema, tUserApply.schema, tUserComment.schema, tUserLike.schema, tUsers.schema, tUserSuggestion.schema, tVersion.schema).reduceLeft(_ ++ _)
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table tAbility
   *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
   *  @param name Database column name SqlType(varchar), Length(255,true)
   *  @param businessId Database column business_id SqlType(varchar), Length(255,true)
   *  @param state Database column state SqlType(int4), Default(0)
   *  @param order Database column order SqlType(int4), Default(0)
   *  @param createTime Database column create_time SqlType(int8) */
  case class rAbility(id: Long, name: String, businessId: String, state: Int = 0, order: Int = 0, createTime: Long)
  /** GetResult implicit for fetching rAbility objects using plain SQL queries */
  implicit def GetResultrAbility(implicit e0: GR[Long], e1: GR[String], e2: GR[Int]): GR[rAbility] = GR{
    prs => import prs._
    rAbility.tupled((<<[Long], <<[String], <<[String], <<[Int], <<[Int], <<[Long]))
  }
  /** Table description of table ability. Objects of this class serve as prototypes for rows in queries. */
  class tAbility(_tableTag: Tag) extends profile.api.Table[rAbility](_tableTag, "ability") {
    def * = (id, name, businessId, state, order, createTime) <> (rAbility.tupled, rAbility.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(name), Rep.Some(businessId), Rep.Some(state), Rep.Some(order), Rep.Some(createTime)).shaped.<>({r=>import r._; _1.map(_=> rAbility.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column name SqlType(varchar), Length(255,true) */
    val name: Rep[String] = column[String]("name", O.Length(255,varying=true))
    /** Database column business_id SqlType(varchar), Length(255,true) */
    val businessId: Rep[String] = column[String]("business_id", O.Length(255,varying=true))
    /** Database column state SqlType(int4), Default(0) */
    val state: Rep[Int] = column[Int]("state", O.Default(0))
    /** Database column order SqlType(int4), Default(0) */
    val order: Rep[Int] = column[Int]("order", O.Default(0))
    /** Database column create_time SqlType(int8) */
    val createTime: Rep[Long] = column[Long]("create_time")
  }
  /** Collection-like TableQuery object for table tAbility */
  lazy val tAbility = new TableQuery(tag => new tAbility(tag))

  /** Entity class storing rows of table tBaseDescription
   *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
   *  @param name Database column name SqlType(varchar), Length(50,true)
   *  @param tag Database column tag SqlType(int4)
   *  @param content Database column content SqlType(text), Default()
   *  @param createTime Database column create_time SqlType(int8) */
  case class rBaseDescription(id: Long, name: String, tag: Int, content: String = "", createTime: Long)
  /** GetResult implicit for fetching rBaseDescription objects using plain SQL queries */
  implicit def GetResultrBaseDescription(implicit e0: GR[Long], e1: GR[String], e2: GR[Int]): GR[rBaseDescription] = GR{
    prs => import prs._
    rBaseDescription.tupled((<<[Long], <<[String], <<[Int], <<[String], <<[Long]))
  }
  /** Table description of table base_description. Objects of this class serve as prototypes for rows in queries. */
  class tBaseDescription(_tableTag: Tag) extends profile.api.Table[rBaseDescription](_tableTag, "base_description") {
    def * = (id, name, tag, content, createTime) <> (rBaseDescription.tupled, rBaseDescription.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(name), Rep.Some(tag), Rep.Some(content), Rep.Some(createTime)).shaped.<>({r=>import r._; _1.map(_=> rBaseDescription.tupled((_1.get, _2.get, _3.get, _4.get, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column name SqlType(varchar), Length(50,true) */
    val name: Rep[String] = column[String]("name", O.Length(50,varying=true))
    /** Database column tag SqlType(int4) */
    val tag: Rep[Int] = column[Int]("tag")
    /** Database column content SqlType(text), Default() */
    val content: Rep[String] = column[String]("content", O.Default(""))
    /** Database column create_time SqlType(int8) */
    val createTime: Rep[Long] = column[Long]("create_time")
  }
  /** Collection-like TableQuery object for table tBaseDescription */
  lazy val tBaseDescription = new TableQuery(tag => new tBaseDescription(tag))

  /** Entity class storing rows of table tDolists
   *  @param tid Database column tid SqlType(bigserial), AutoInc, PrimaryKey
   *  @param name Database column name SqlType(varchar), Length(255,true)
   *  @param todo Database column todo SqlType(varchar), Length(255,true) */
  case class rDolists(tid: Long, name: String, todo: String)
  /** GetResult implicit for fetching rDolists objects using plain SQL queries */
  implicit def GetResultrDolists(implicit e0: GR[Long], e1: GR[String]): GR[rDolists] = GR{
    prs => import prs._
    rDolists.tupled((<<[Long], <<[String], <<[String]))
  }
  /** Table description of table dolists. Objects of this class serve as prototypes for rows in queries. */
  class tDolists(_tableTag: Tag) extends profile.api.Table[rDolists](_tableTag, "dolists") {
    def * = (tid, name, todo) <> (rDolists.tupled, rDolists.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(tid), Rep.Some(name), Rep.Some(todo)).shaped.<>({r=>import r._; _1.map(_=> rDolists.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column tid SqlType(bigserial), AutoInc, PrimaryKey */
    val tid: Rep[Long] = column[Long]("tid", O.AutoInc, O.PrimaryKey)
    /** Database column name SqlType(varchar), Length(255,true) */
    val name: Rep[String] = column[String]("name", O.Length(255,varying=true))
    /** Database column todo SqlType(varchar), Length(255,true) */
    val todo: Rep[String] = column[String]("todo", O.Length(255,varying=true))
  }
  /** Collection-like TableQuery object for table tDolists */
  lazy val tDolists = new TableQuery(tag => new tDolists(tag))

  /** Entity class storing rows of table tGpuApply
   *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
   *  @param username Database column username SqlType(varchar), Length(255,true)
   *  @param block Database column block SqlType(int4)
   *  @param date Database column date SqlType(varchar), Length(255,true)
   *  @param starttime Database column starttime SqlType(int4) */
  case class rGpuApply(id: Long, username: String, block: Int, date: String, starttime: Int)
  /** GetResult implicit for fetching rGpuApply objects using plain SQL queries */
  implicit def GetResultrGpuApply(implicit e0: GR[Long], e1: GR[String], e2: GR[Int]): GR[rGpuApply] = GR{
    prs => import prs._
    rGpuApply.tupled((<<[Long], <<[String], <<[Int], <<[String], <<[Int]))
  }
  /** Table description of table gpu_apply. Objects of this class serve as prototypes for rows in queries. */
  class tGpuApply(_tableTag: Tag) extends profile.api.Table[rGpuApply](_tableTag, "gpu_apply") {
    def * = (id, username, block, date, starttime) <> (rGpuApply.tupled, rGpuApply.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(username), Rep.Some(block), Rep.Some(date), Rep.Some(starttime)).shaped.<>({r=>import r._; _1.map(_=> rGpuApply.tupled((_1.get, _2.get, _3.get, _4.get, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column username SqlType(varchar), Length(255,true) */
    val username: Rep[String] = column[String]("username", O.Length(255,varying=true))
    /** Database column block SqlType(int4) */
    val block: Rep[Int] = column[Int]("block")
    /** Database column date SqlType(varchar), Length(255,true) */
    val date: Rep[String] = column[String]("date", O.Length(255,varying=true))
    /** Database column starttime SqlType(int4) */
    val starttime: Rep[Int] = column[Int]("starttime")
  }
  /** Collection-like TableQuery object for table tGpuApply */
  lazy val tGpuApply = new TableQuery(tag => new tGpuApply(tag))

  /** Entity class storing rows of table tGpuManager
   *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
   *  @param username Database column username SqlType(varchar), Length(255,true)
   *  @param password Database column password SqlType(varchar), Length(255,true) */
  case class rGpuManager(id: Long, username: String, password: String)
  /** GetResult implicit for fetching rGpuManager objects using plain SQL queries */
  implicit def GetResultrGpuManager(implicit e0: GR[Long], e1: GR[String]): GR[rGpuManager] = GR{
    prs => import prs._
    rGpuManager.tupled((<<[Long], <<[String], <<[String]))
  }
  /** Table description of table gpu_manager. Objects of this class serve as prototypes for rows in queries. */
  class tGpuManager(_tableTag: Tag) extends profile.api.Table[rGpuManager](_tableTag, "gpu_manager") {
    def * = (id, username, password) <> (rGpuManager.tupled, rGpuManager.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(username), Rep.Some(password)).shaped.<>({r=>import r._; _1.map(_=> rGpuManager.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column username SqlType(varchar), Length(255,true) */
    val username: Rep[String] = column[String]("username", O.Length(255,varying=true))
    /** Database column password SqlType(varchar), Length(255,true) */
    val password: Rep[String] = column[String]("password", O.Length(255,varying=true))
  }
  /** Collection-like TableQuery object for table tGpuManager */
  lazy val tGpuManager = new TableQuery(tag => new tGpuManager(tag))

  /** Entity class storing rows of table tGpuUser
   *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
   *  @param username Database column username SqlType(varchar), Length(255,true)
   *  @param password Database column password SqlType(varchar), Length(255,true)
   *  @param cardtime Database column cardtime SqlType(float8) */
  case class rGpuUser(id: Long, username: String, password: String, cardtime: Double)
  /** GetResult implicit for fetching rGpuUser objects using plain SQL queries */
  implicit def GetResultrGpuUser(implicit e0: GR[Long], e1: GR[String], e2: GR[Double]): GR[rGpuUser] = GR{
    prs => import prs._
    rGpuUser.tupled((<<[Long], <<[String], <<[String], <<[Double]))
  }
  /** Table description of table gpu_user. Objects of this class serve as prototypes for rows in queries. */
  class tGpuUser(_tableTag: Tag) extends profile.api.Table[rGpuUser](_tableTag, "gpu_user") {
    def * = (id, username, password, cardtime) <> (rGpuUser.tupled, rGpuUser.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(username), Rep.Some(password), Rep.Some(cardtime)).shaped.<>({r=>import r._; _1.map(_=> rGpuUser.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column username SqlType(varchar), Length(255,true) */
    val username: Rep[String] = column[String]("username", O.Length(255,varying=true))
    /** Database column password SqlType(varchar), Length(255,true) */
    val password: Rep[String] = column[String]("password", O.Length(255,varying=true))
    /** Database column cardtime SqlType(float8) */
    val cardtime: Rep[Double] = column[Double]("cardtime")
  }
  /** Collection-like TableQuery object for table tGpuUser */
  lazy val tGpuUser = new TableQuery(tag => new tGpuUser(tag))

  /** Entity class storing rows of table tResource
   *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
   *  @param name Database column name SqlType(varchar), Length(255,true)
   *  @param url Database column url SqlType(varchar), Length(255,true)
   *  @param nameCn Database column name_cn SqlType(varchar), Length(255,true)
   *  @param origin Database column origin SqlType(int4)
   *  @param resourceFor Database column resource_for SqlType(int4) */
  case class rResource(id: Long, name: String, url: String, nameCn: String, origin: Int, resourceFor: Int)
  /** GetResult implicit for fetching rResource objects using plain SQL queries */
  implicit def GetResultrResource(implicit e0: GR[Long], e1: GR[String], e2: GR[Int]): GR[rResource] = GR{
    prs => import prs._
    rResource.tupled((<<[Long], <<[String], <<[String], <<[String], <<[Int], <<[Int]))
  }
  /** Table description of table resource. Objects of this class serve as prototypes for rows in queries. */
  class tResource(_tableTag: Tag) extends profile.api.Table[rResource](_tableTag, "resource") {
    def * = (id, name, url, nameCn, origin, resourceFor) <> (rResource.tupled, rResource.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(name), Rep.Some(url), Rep.Some(nameCn), Rep.Some(origin), Rep.Some(resourceFor)).shaped.<>({r=>import r._; _1.map(_=> rResource.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column name SqlType(varchar), Length(255,true) */
    val name: Rep[String] = column[String]("name", O.Length(255,varying=true))
    /** Database column url SqlType(varchar), Length(255,true) */
    val url: Rep[String] = column[String]("url", O.Length(255,varying=true))
    /** Database column name_cn SqlType(varchar), Length(255,true) */
    val nameCn: Rep[String] = column[String]("name_cn", O.Length(255,varying=true))
    /** Database column origin SqlType(int4) */
    val origin: Rep[Int] = column[Int]("origin")
    /** Database column resource_for SqlType(int4) */
    val resourceFor: Rep[Int] = column[Int]("resource_for")
  }
  /** Collection-like TableQuery object for table tResource */
  lazy val tResource = new TableQuery(tag => new tResource(tag))

  /** Entity class storing rows of table tResourceAttach
   *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
   *  @param fileName Database column file_name SqlType(varchar), Length(255,true)
   *  @param fileUrl Database column file_url SqlType(varchar), Length(255,true)
   *  @param versionId Database column version_id SqlType(int8), Default(None)
   *  @param order Database column order SqlType(int4), Default(None)
   *  @param businessId Database column business_id SqlType(varchar), Length(255,true) */
  case class rResourceAttach(id: Long, fileName: String, fileUrl: String, versionId: Option[Long] = None, order: Option[Int] = None, businessId: String)
  /** GetResult implicit for fetching rResourceAttach objects using plain SQL queries */
  implicit def GetResultrResourceAttach(implicit e0: GR[Long], e1: GR[String], e2: GR[Option[Long]], e3: GR[Option[Int]]): GR[rResourceAttach] = GR{
    prs => import prs._
    rResourceAttach.tupled((<<[Long], <<[String], <<[String], <<?[Long], <<?[Int], <<[String]))
  }
  /** Table description of table resource_attach. Objects of this class serve as prototypes for rows in queries. */
  class tResourceAttach(_tableTag: Tag) extends profile.api.Table[rResourceAttach](_tableTag, "resource_attach") {
    def * = (id, fileName, fileUrl, versionId, order, businessId) <> (rResourceAttach.tupled, rResourceAttach.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(fileName), Rep.Some(fileUrl), versionId, order, Rep.Some(businessId)).shaped.<>({r=>import r._; _1.map(_=> rResourceAttach.tupled((_1.get, _2.get, _3.get, _4, _5, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column file_name SqlType(varchar), Length(255,true) */
    val fileName: Rep[String] = column[String]("file_name", O.Length(255,varying=true))
    /** Database column file_url SqlType(varchar), Length(255,true) */
    val fileUrl: Rep[String] = column[String]("file_url", O.Length(255,varying=true))
    /** Database column version_id SqlType(int8), Default(None) */
    val versionId: Rep[Option[Long]] = column[Option[Long]]("version_id", O.Default(None))
    /** Database column order SqlType(int4), Default(None) */
    val order: Rep[Option[Int]] = column[Option[Int]]("order", O.Default(None))
    /** Database column business_id SqlType(varchar), Length(255,true) */
    val businessId: Rep[String] = column[String]("business_id", O.Length(255,varying=true))
  }
  /** Collection-like TableQuery object for table tResourceAttach */
  lazy val tResourceAttach = new TableQuery(tag => new tResourceAttach(tag))

  /** Entity class storing rows of table tResourceType
   *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
   *  @param name Database column name SqlType(varchar), Length(255,true)
   *  @param abilityId Database column ability_id SqlType(int8)
   *  @param order Database column order SqlType(int4), Default(0)
   *  @param createTime Database column create_time SqlType(int8)
   *  @param businessId Database column business_id SqlType(varchar), Length(255,true) */
  case class rResourceType(id: Long, name: String, abilityId: Long, order: Int = 0, createTime: Long, businessId: String)
  /** GetResult implicit for fetching rResourceType objects using plain SQL queries */
  implicit def GetResultrResourceType(implicit e0: GR[Long], e1: GR[String], e2: GR[Int]): GR[rResourceType] = GR{
    prs => import prs._
    rResourceType.tupled((<<[Long], <<[String], <<[Long], <<[Int], <<[Long], <<[String]))
  }
  /** Table description of table resource_type. Objects of this class serve as prototypes for rows in queries. */
  class tResourceType(_tableTag: Tag) extends profile.api.Table[rResourceType](_tableTag, "resource_type") {
    def * = (id, name, abilityId, order, createTime, businessId) <> (rResourceType.tupled, rResourceType.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(name), Rep.Some(abilityId), Rep.Some(order), Rep.Some(createTime), Rep.Some(businessId)).shaped.<>({r=>import r._; _1.map(_=> rResourceType.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column name SqlType(varchar), Length(255,true) */
    val name: Rep[String] = column[String]("name", O.Length(255,varying=true))
    /** Database column ability_id SqlType(int8) */
    val abilityId: Rep[Long] = column[Long]("ability_id")
    /** Database column order SqlType(int4), Default(0) */
    val order: Rep[Int] = column[Int]("order", O.Default(0))
    /** Database column create_time SqlType(int8) */
    val createTime: Rep[Long] = column[Long]("create_time")
    /** Database column business_id SqlType(varchar), Length(255,true) */
    val businessId: Rep[String] = column[String]("business_id", O.Length(255,varying=true))
  }
  /** Collection-like TableQuery object for table tResourceType */
  lazy val tResourceType = new TableQuery(tag => new tResourceType(tag))

  /** Entity class storing rows of table tUser
   *  @param uid Database column uid SqlType(bigserial), AutoInc, PrimaryKey
   *  @param nickname Database column nickname SqlType(varchar), Length(255,true)
   *  @param headimgurl Database column headimgurl SqlType(varchar), Length(1024,true), Default()
   *  @param sex Database column sex SqlType(int4), Default(0)
   *  @param state Database column state SqlType(int4), Default(0)
   *  @param password Database column password SqlType(varchar), Length(255,true), Default()
   *  @param createTime Database column create_time SqlType(int8) */
  case class rUser(uid: Long, nickname: String, headimgurl: String = "", sex: Int = 0, state: Int = 0, password: String = "", createTime: Long)
  /** GetResult implicit for fetching rUser objects using plain SQL queries */
  implicit def GetResultrUser(implicit e0: GR[Long], e1: GR[String], e2: GR[Int]): GR[rUser] = GR{
    prs => import prs._
    rUser.tupled((<<[Long], <<[String], <<[String], <<[Int], <<[Int], <<[String], <<[Long]))
  }
  /** Table description of table user. Objects of this class serve as prototypes for rows in queries. */
  class tUser(_tableTag: Tag) extends profile.api.Table[rUser](_tableTag, "user") {
    def * = (uid, nickname, headimgurl, sex, state, password, createTime) <> (rUser.tupled, rUser.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(uid), Rep.Some(nickname), Rep.Some(headimgurl), Rep.Some(sex), Rep.Some(state), Rep.Some(password), Rep.Some(createTime)).shaped.<>({r=>import r._; _1.map(_=> rUser.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column uid SqlType(bigserial), AutoInc, PrimaryKey */
    val uid: Rep[Long] = column[Long]("uid", O.AutoInc, O.PrimaryKey)
    /** Database column nickname SqlType(varchar), Length(255,true) */
    val nickname: Rep[String] = column[String]("nickname", O.Length(255,varying=true))
    /** Database column headimgurl SqlType(varchar), Length(1024,true), Default() */
    val headimgurl: Rep[String] = column[String]("headimgurl", O.Length(1024,varying=true), O.Default(""))
    /** Database column sex SqlType(int4), Default(0) */
    val sex: Rep[Int] = column[Int]("sex", O.Default(0))
    /** Database column state SqlType(int4), Default(0) */
    val state: Rep[Int] = column[Int]("state", O.Default(0))
    /** Database column password SqlType(varchar), Length(255,true), Default() */
    val password: Rep[String] = column[String]("password", O.Length(255,varying=true), O.Default(""))
    /** Database column create_time SqlType(int8) */
    val createTime: Rep[Long] = column[Long]("create_time")
  }
  /** Collection-like TableQuery object for table tUser */
  lazy val tUser = new TableQuery(tag => new tUser(tag))

  /** Entity class storing rows of table tUserApply
   *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
   *  @param userId Database column user_id SqlType(varchar), Length(255,true)
   *  @param title Database column title SqlType(varchar), Length(100,true)
   *  @param abilityId Database column ability_id SqlType(varchar), Length(255,true)
   *  @param name Database column name SqlType(varchar), Length(50,true)
   *  @param company Database column company SqlType(varchar), Length(255,true)
   *  @param mobile Database column mobile SqlType(varchar), Length(50,true)
   *  @param email Database column email SqlType(varchar), Length(50,true)
   *  @param state Database column state SqlType(int4), Default(0)
   *  @param readOrNot Database column read_or_not SqlType(bool), Default(false)
   *  @param createTime Database column create_time SqlType(int8) */
  case class rUserApply(id: Long, userId: String, title: String, abilityId: String, name: String, company: String, mobile: String, email: String, state: Int = 0, readOrNot: Boolean = false, createTime: Long)
  /** GetResult implicit for fetching rUserApply objects using plain SQL queries */
  implicit def GetResultrUserApply(implicit e0: GR[Long], e1: GR[String], e2: GR[Int], e3: GR[Boolean]): GR[rUserApply] = GR{
    prs => import prs._
    rUserApply.tupled((<<[Long], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[Int], <<[Boolean], <<[Long]))
  }
  /** Table description of table user_apply. Objects of this class serve as prototypes for rows in queries. */
  class tUserApply(_tableTag: Tag) extends profile.api.Table[rUserApply](_tableTag, "user_apply") {
    def * = (id, userId, title, abilityId, name, company, mobile, email, state, readOrNot, createTime) <> (rUserApply.tupled, rUserApply.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(userId), Rep.Some(title), Rep.Some(abilityId), Rep.Some(name), Rep.Some(company), Rep.Some(mobile), Rep.Some(email), Rep.Some(state), Rep.Some(readOrNot), Rep.Some(createTime)).shaped.<>({r=>import r._; _1.map(_=> rUserApply.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get, _11.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column user_id SqlType(varchar), Length(255,true) */
    val userId: Rep[String] = column[String]("user_id", O.Length(255,varying=true))
    /** Database column title SqlType(varchar), Length(100,true) */
    val title: Rep[String] = column[String]("title", O.Length(100,varying=true))
    /** Database column ability_id SqlType(varchar), Length(255,true) */
    val abilityId: Rep[String] = column[String]("ability_id", O.Length(255,varying=true))
    /** Database column name SqlType(varchar), Length(50,true) */
    val name: Rep[String] = column[String]("name", O.Length(50,varying=true))
    /** Database column company SqlType(varchar), Length(255,true) */
    val company: Rep[String] = column[String]("company", O.Length(255,varying=true))
    /** Database column mobile SqlType(varchar), Length(50,true) */
    val mobile: Rep[String] = column[String]("mobile", O.Length(50,varying=true))
    /** Database column email SqlType(varchar), Length(50,true) */
    val email: Rep[String] = column[String]("email", O.Length(50,varying=true))
    /** Database column state SqlType(int4), Default(0) */
    val state: Rep[Int] = column[Int]("state", O.Default(0))
    /** Database column read_or_not SqlType(bool), Default(false) */
    val readOrNot: Rep[Boolean] = column[Boolean]("read_or_not", O.Default(false))
    /** Database column create_time SqlType(int8) */
    val createTime: Rep[Long] = column[Long]("create_time")
  }
  /** Collection-like TableQuery object for table tUserApply */
  lazy val tUserApply = new TableQuery(tag => new tUserApply(tag))

  /** Entity class storing rows of table tUserComment
   *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
   *  @param uid Database column uid SqlType(int8)
   *  @param nickname Database column nickname SqlType(varchar), Length(255,true)
   *  @param resourceOrigin Database column resource_origin SqlType(int4)
   *  @param resourceId Database column resource_id SqlType(varchar), Length(63,true)
   *  @param targetId Database column target_id SqlType(varchar), Length(63,true)
   *  @param comment Database column comment SqlType(varchar), Length(255,true)
   *  @param createTime Database column create_time SqlType(int8)
   *  @param title Database column title SqlType(varchar), Length(255,true)
   *  @param state Database column state SqlType(int4), Default(0)
   *  @param articleUrl Database column article_url SqlType(varchar), Length(255,true), Default() */
  case class rUserComment(id: Long, uid: Long, nickname: String, resourceOrigin: Int, resourceId: String, targetId: String, comment: String, createTime: Long, title: String, state: Int = 0, articleUrl: String = "")
  /** GetResult implicit for fetching rUserComment objects using plain SQL queries */
  implicit def GetResultrUserComment(implicit e0: GR[Long], e1: GR[String], e2: GR[Int]): GR[rUserComment] = GR{
    prs => import prs._
    rUserComment.tupled((<<[Long], <<[Long], <<[String], <<[Int], <<[String], <<[String], <<[String], <<[Long], <<[String], <<[Int], <<[String]))
  }
  /** Table description of table user_comment. Objects of this class serve as prototypes for rows in queries. */
  class tUserComment(_tableTag: Tag) extends profile.api.Table[rUserComment](_tableTag, "user_comment") {
    def * = (id, uid, nickname, resourceOrigin, resourceId, targetId, comment, createTime, title, state, articleUrl) <> (rUserComment.tupled, rUserComment.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(uid), Rep.Some(nickname), Rep.Some(resourceOrigin), Rep.Some(resourceId), Rep.Some(targetId), Rep.Some(comment), Rep.Some(createTime), Rep.Some(title), Rep.Some(state), Rep.Some(articleUrl)).shaped.<>({r=>import r._; _1.map(_=> rUserComment.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get, _11.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column uid SqlType(int8) */
    val uid: Rep[Long] = column[Long]("uid")
    /** Database column nickname SqlType(varchar), Length(255,true) */
    val nickname: Rep[String] = column[String]("nickname", O.Length(255,varying=true))
    /** Database column resource_origin SqlType(int4) */
    val resourceOrigin: Rep[Int] = column[Int]("resource_origin")
    /** Database column resource_id SqlType(varchar), Length(63,true) */
    val resourceId: Rep[String] = column[String]("resource_id", O.Length(63,varying=true))
    /** Database column target_id SqlType(varchar), Length(63,true) */
    val targetId: Rep[String] = column[String]("target_id", O.Length(63,varying=true))
    /** Database column comment SqlType(varchar), Length(255,true) */
    val comment: Rep[String] = column[String]("comment", O.Length(255,varying=true))
    /** Database column create_time SqlType(int8) */
    val createTime: Rep[Long] = column[Long]("create_time")
    /** Database column title SqlType(varchar), Length(255,true) */
    val title: Rep[String] = column[String]("title", O.Length(255,varying=true))
    /** Database column state SqlType(int4), Default(0) */
    val state: Rep[Int] = column[Int]("state", O.Default(0))
    /** Database column article_url SqlType(varchar), Length(255,true), Default() */
    val articleUrl: Rep[String] = column[String]("article_url", O.Length(255,varying=true), O.Default(""))
  }
  /** Collection-like TableQuery object for table tUserComment */
  lazy val tUserComment = new TableQuery(tag => new tUserComment(tag))

  /** Entity class storing rows of table tUserLike
   *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
   *  @param uid Database column uid SqlType(int8)
   *  @param nickname Database column nickname SqlType(varchar), Length(255,true)
   *  @param resourceOrigin Database column resource_origin SqlType(int4)
   *  @param resourceId Database column resource_id SqlType(varchar), Length(63,true)
   *  @param targetId Database column target_id SqlType(varchar), Length(63,true)
   *  @param state Database column state SqlType(int4)
   *  @param createTime Database column create_time SqlType(int8)
   *  @param title Database column title SqlType(varchar), Length(255,true)
   *  @param articleUrl Database column article_url SqlType(varchar), Length(255,true) */
  case class rUserLike(id: Long, uid: Long, nickname: String, resourceOrigin: Int, resourceId: String, targetId: String, state: Int, createTime: Long, title: String, articleUrl: String)
  /** GetResult implicit for fetching rUserLike objects using plain SQL queries */
  implicit def GetResultrUserLike(implicit e0: GR[Long], e1: GR[String], e2: GR[Int]): GR[rUserLike] = GR{
    prs => import prs._
    rUserLike.tupled((<<[Long], <<[Long], <<[String], <<[Int], <<[String], <<[String], <<[Int], <<[Long], <<[String], <<[String]))
  }
  /** Table description of table user_like. Objects of this class serve as prototypes for rows in queries. */
  class tUserLike(_tableTag: Tag) extends profile.api.Table[rUserLike](_tableTag, "user_like") {
    def * = (id, uid, nickname, resourceOrigin, resourceId, targetId, state, createTime, title, articleUrl) <> (rUserLike.tupled, rUserLike.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(uid), Rep.Some(nickname), Rep.Some(resourceOrigin), Rep.Some(resourceId), Rep.Some(targetId), Rep.Some(state), Rep.Some(createTime), Rep.Some(title), Rep.Some(articleUrl)).shaped.<>({r=>import r._; _1.map(_=> rUserLike.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column uid SqlType(int8) */
    val uid: Rep[Long] = column[Long]("uid")
    /** Database column nickname SqlType(varchar), Length(255,true) */
    val nickname: Rep[String] = column[String]("nickname", O.Length(255,varying=true))
    /** Database column resource_origin SqlType(int4) */
    val resourceOrigin: Rep[Int] = column[Int]("resource_origin")
    /** Database column resource_id SqlType(varchar), Length(63,true) */
    val resourceId: Rep[String] = column[String]("resource_id", O.Length(63,varying=true))
    /** Database column target_id SqlType(varchar), Length(63,true) */
    val targetId: Rep[String] = column[String]("target_id", O.Length(63,varying=true))
    /** Database column state SqlType(int4) */
    val state: Rep[Int] = column[Int]("state")
    /** Database column create_time SqlType(int8) */
    val createTime: Rep[Long] = column[Long]("create_time")
    /** Database column title SqlType(varchar), Length(255,true) */
    val title: Rep[String] = column[String]("title", O.Length(255,varying=true))
    /** Database column article_url SqlType(varchar), Length(255,true) */
    val articleUrl: Rep[String] = column[String]("article_url", O.Length(255,varying=true))
  }
  /** Collection-like TableQuery object for table tUserLike */
  lazy val tUserLike = new TableQuery(tag => new tUserLike(tag))

  /** Entity class storing rows of table tUsers
   *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
   *  @param name Database column name SqlType(varchar), Length(255,true)
   *  @param psw Database column psw SqlType(varchar), Length(255,true) */
  case class rUsers(id: Long, name: String, psw: String)
  /** GetResult implicit for fetching rUsers objects using plain SQL queries */
  implicit def GetResultrUsers(implicit e0: GR[Long], e1: GR[String]): GR[rUsers] = GR{
    prs => import prs._
    rUsers.tupled((<<[Long], <<[String], <<[String]))
  }
  /** Table description of table users. Objects of this class serve as prototypes for rows in queries. */
  class tUsers(_tableTag: Tag) extends profile.api.Table[rUsers](_tableTag, "users") {
    def * = (id, name, psw) <> (rUsers.tupled, rUsers.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(name), Rep.Some(psw)).shaped.<>({r=>import r._; _1.map(_=> rUsers.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column name SqlType(varchar), Length(255,true) */
    val name: Rep[String] = column[String]("name", O.Length(255,varying=true))
    /** Database column psw SqlType(varchar), Length(255,true) */
    val psw: Rep[String] = column[String]("psw", O.Length(255,varying=true))
  }
  /** Collection-like TableQuery object for table tUsers */
  lazy val tUsers = new TableQuery(tag => new tUsers(tag))

  /** Entity class storing rows of table tUserSuggestion
   *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
   *  @param replyId Database column reply_id SqlType(int8), Default(0)
   *  @param userType Database column user_type SqlType(varchar), Length(64,true), Default()
   *  @param userId Database column user_id SqlType(varchar), Length(64,true), Default()
   *  @param userName Database column user_name SqlType(varchar), Length(50,true), Default()
   *  @param userShortName Database column user_short_name SqlType(varchar), Length(50,true), Default()
   *  @param userMobile Database column user_mobile SqlType(varchar), Length(12,true), Default()
   *  @param userSex Database column user_sex SqlType(int2), Default(0)
   *  @param userCode Database column user_code SqlType(varchar), Length(128,true), Default()
   *  @param title Database column title SqlType(varchar), Length(100,true), Default()
   *  @param content Database column content SqlType(text), Default()
   *  @param createTime Database column create_time SqlType(int8), Default(0)
   *  @param versionId Database column version_id SqlType(int8), Default(0)
   *  @param state Database column state SqlType(int4), Default(0) */
  case class rUserSuggestion(id: Long, replyId: Long = 0L, userType: String = "", userId: String = "", userName: String = "", userShortName: String = "", userMobile: String = "", userSex: Short = 0, userCode: String = "", title: String = "", content: String = "", createTime: Long = 0L, versionId: Long = 0L, state: Int = 0)
  /** GetResult implicit for fetching rUserSuggestion objects using plain SQL queries */
  implicit def GetResultrUserSuggestion(implicit e0: GR[Long], e1: GR[String], e2: GR[Short], e3: GR[Int]): GR[rUserSuggestion] = GR{
    prs => import prs._
    rUserSuggestion.tupled((<<[Long], <<[Long], <<[String], <<[String], <<[String], <<[String], <<[String], <<[Short], <<[String], <<[String], <<[String], <<[Long], <<[Long], <<[Int]))
  }
  /** Table description of table user_suggestion. Objects of this class serve as prototypes for rows in queries. */
  class tUserSuggestion(_tableTag: Tag) extends profile.api.Table[rUserSuggestion](_tableTag, "user_suggestion") {
    def * = (id, replyId, userType, userId, userName, userShortName, userMobile, userSex, userCode, title, content, createTime, versionId, state) <> (rUserSuggestion.tupled, rUserSuggestion.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(replyId), Rep.Some(userType), Rep.Some(userId), Rep.Some(userName), Rep.Some(userShortName), Rep.Some(userMobile), Rep.Some(userSex), Rep.Some(userCode), Rep.Some(title), Rep.Some(content), Rep.Some(createTime), Rep.Some(versionId), Rep.Some(state)).shaped.<>({r=>import r._; _1.map(_=> rUserSuggestion.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get, _11.get, _12.get, _13.get, _14.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column reply_id SqlType(int8), Default(0) */
    val replyId: Rep[Long] = column[Long]("reply_id", O.Default(0L))
    /** Database column user_type SqlType(varchar), Length(64,true), Default() */
    val userType: Rep[String] = column[String]("user_type", O.Length(64,varying=true), O.Default(""))
    /** Database column user_id SqlType(varchar), Length(64,true), Default() */
    val userId: Rep[String] = column[String]("user_id", O.Length(64,varying=true), O.Default(""))
    /** Database column user_name SqlType(varchar), Length(50,true), Default() */
    val userName: Rep[String] = column[String]("user_name", O.Length(50,varying=true), O.Default(""))
    /** Database column user_short_name SqlType(varchar), Length(50,true), Default() */
    val userShortName: Rep[String] = column[String]("user_short_name", O.Length(50,varying=true), O.Default(""))
    /** Database column user_mobile SqlType(varchar), Length(12,true), Default() */
    val userMobile: Rep[String] = column[String]("user_mobile", O.Length(12,varying=true), O.Default(""))
    /** Database column user_sex SqlType(int2), Default(0) */
    val userSex: Rep[Short] = column[Short]("user_sex", O.Default(0))
    /** Database column user_code SqlType(varchar), Length(128,true), Default() */
    val userCode: Rep[String] = column[String]("user_code", O.Length(128,varying=true), O.Default(""))
    /** Database column title SqlType(varchar), Length(100,true), Default() */
    val title: Rep[String] = column[String]("title", O.Length(100,varying=true), O.Default(""))
    /** Database column content SqlType(text), Default() */
    val content: Rep[String] = column[String]("content", O.Default(""))
    /** Database column create_time SqlType(int8), Default(0) */
    val createTime: Rep[Long] = column[Long]("create_time", O.Default(0L))
    /** Database column version_id SqlType(int8), Default(0) */
    val versionId: Rep[Long] = column[Long]("version_id", O.Default(0L))
    /** Database column state SqlType(int4), Default(0) */
    val state: Rep[Int] = column[Int]("state", O.Default(0))
  }
  /** Collection-like TableQuery object for table tUserSuggestion */
  lazy val tUserSuggestion = new TableQuery(tag => new tUserSuggestion(tag))

  /** Entity class storing rows of table tVersion
   *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
   *  @param description Database column description SqlType(text)
   *  @param author Database column author SqlType(varchar), Length(128,true)
   *  @param resourceId Database column resource_id SqlType(int8)
   *  @param abilityId Database column ability_id SqlType(int8)
   *  @param state Database column state SqlType(int4), Default(0)
   *  @param createTime Database column create_time SqlType(int8)
   *  @param businessId Database column business_id SqlType(varchar), Length(255,true)
   *  @param versionId Database column version_id SqlType(varchar), Length(63,true) */
  case class rVersion(id: Long, description: String, author: String, resourceId: Long, abilityId: Long, state: Int = 0, createTime: Long, businessId: String, versionId: String)
  /** GetResult implicit for fetching rVersion objects using plain SQL queries */
  implicit def GetResultrVersion(implicit e0: GR[Long], e1: GR[String], e2: GR[Int]): GR[rVersion] = GR{
    prs => import prs._
    rVersion.tupled((<<[Long], <<[String], <<[String], <<[Long], <<[Long], <<[Int], <<[Long], <<[String], <<[String]))
  }
  /** Table description of table version. Objects of this class serve as prototypes for rows in queries. */
  class tVersion(_tableTag: Tag) extends profile.api.Table[rVersion](_tableTag, "version") {
    def * = (id, description, author, resourceId, abilityId, state, createTime, businessId, versionId) <> (rVersion.tupled, rVersion.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(description), Rep.Some(author), Rep.Some(resourceId), Rep.Some(abilityId), Rep.Some(state), Rep.Some(createTime), Rep.Some(businessId), Rep.Some(versionId)).shaped.<>({r=>import r._; _1.map(_=> rVersion.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column description SqlType(text) */
    val description: Rep[String] = column[String]("description")
    /** Database column author SqlType(varchar), Length(128,true) */
    val author: Rep[String] = column[String]("author", O.Length(128,varying=true))
    /** Database column resource_id SqlType(int8) */
    val resourceId: Rep[Long] = column[Long]("resource_id")
    /** Database column ability_id SqlType(int8) */
    val abilityId: Rep[Long] = column[Long]("ability_id")
    /** Database column state SqlType(int4), Default(0) */
    val state: Rep[Int] = column[Int]("state", O.Default(0))
    /** Database column create_time SqlType(int8) */
    val createTime: Rep[Long] = column[Long]("create_time")
    /** Database column business_id SqlType(varchar), Length(255,true) */
    val businessId: Rep[String] = column[String]("business_id", O.Length(255,varying=true))
    /** Database column version_id SqlType(varchar), Length(63,true) */
    val versionId: Rep[String] = column[String]("version_id", O.Length(63,varying=true))
  }
  /** Collection-like TableQuery object for table tVersion */
  lazy val tVersion = new TableQuery(tag => new tVersion(tag))
}
