<template>
  <div id="app">
    <Wishes />
  </div>
</template>

<script>
  import Wishes from './components/Wishes'
  const app = {
    name: 'app',
    components: {
      Wishes
    },
    data: () => {
      return {

      }
    }
  }

  export default app
</script>

<style>
  [v-cloak] {display: none;}
</style>
