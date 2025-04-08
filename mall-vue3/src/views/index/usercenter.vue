<template>
  <div class="user">

    <Header />

    <div class="user-content">
      <div class="user-content-left">
        <MineInfosView />
      </div>
    </div>

    <Footer />

  </div>
</template>
<script>
import Header from '/@/views/index/components/header.vue'
import Footer from '/@/views/index/components/footer.vue'
import MineInfosView from '/@/views/index/user/mine-infos-view.vue'

export default {
  components: {
    MineInfosView,
    Footer,
    Header
  },
  data () {
    return {
      collapsed: false
    }
  }
}
</script>
<style scoped lang="less">

.user {
  display: block;
}

.user-content {
  //background-color: #2a9a44;
  max-width: 1200px;
  min-width: 800px;
  margin: 80px auto;
}
</style>
