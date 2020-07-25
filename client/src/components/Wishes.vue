<template>
    <div>
        <h1 class="title">Önskelista</h1>       
        <section class="wishlistapp"> 
                <header class="header">
                    <input class="new-wish"
                        autofocus autocomplete="off"
                        :placeholder="this.inputPlaceholder"
                        v-model="newWish"
                        @keyup.enter="addWish">
                        <button class="add-wish-button" @click="addWish">Lägg till</button>
                </header>
                <section class="main" v-show="wishes.length" v-cloak>
                    <ul class="wish-list">
                        <li v-for="wish in filteredWishes"
                        class="wish"
                        :key="wish.id"
                        :class="{ completed: wish.completed }">
                            <div class="view">
                                <input class="toggle" type="checkbox" v-model="wish.completed" @change="completedWish(wish)">
                                <label>{{ wish.title }}</label>
                                <button class="destroy" @click="removeWish(wish)">Ta bort</button>
                            </div>
                        </li> 
                    </ul>
                </section>            
        </section>
        <div v-if="error" class="error" @click="handleErrorClick">
            ERROR: {{this.error}}
        </div>
    </div>
</template>

<script>

// visibility filters
  let filters = {
    all: function (wishes) {
      return wishes
    },
    active: function (wishes) {
      return wishes.filter(function (wish) {
        return !wish.completed
      })
    },
    completed: function (wishes) {
      return wishes.filter(function (wish) {
        return wish.completed
      })
    }
  }

// app Vue instance
  const Wishes = {
    name: 'Wishes',
    props: {
      activeUser: Object
    },
    
    // app initial state
    data: function() {
      return {
        wishes: [],
        newWish: '',
        editedWish: null,
        visibility: 'all',
        loading: true,
        error: null,
      }
    },

    mounted() {
      // inject some startup data
      this.wishes = [{title: 'My little Pony', completed:false},{title: 'Solstol', completed:false}];
      // hide the loading message
      this.loading = false;
    },

    // computed properties
    // http://vuejs.org/guide/computed.html
    computed: {
      filteredWishes: function () {
        return filters[this.visibility](this.wishes)
      },
      remaining: function () {
        return filters.active(this.wishes).length
      },
      allDone: {
        get: function () {
          return this.remaining === 0
        },
        set: function (value) {
          this.wishes.forEach(function (wish) {
            wish.completed = value
          })
        }
      },
      userEmail: function () {
        return this.activeUser ? this.activeUser.email : ''
      },
      inputPlaceholder: function () {
        return this.activeUser ? this.activeUser.given_name + ', önskning' : 'Önskning?'
      }
    },

    filters: {
      pluralize: function (n) {
        return n === 1 ? 'item' : 'items'
      }
    },

    // methods that implement data logic.
    // note there's no DOM manipulation here at all.
    methods: {

      addWish: function () {
        var value = this.newWish && this.newWish.trim()
        if (!value) {
          return
        }

        this.wishes.push({
          title: value,
          completed: false
        });

        this.newWish = ''
      },

      setVisibility: function(vis) {
        this.visibility = vis
      },

      //completeWish (wish) {
      //},

      removeWish: function (wish) { // notice NOT using "=>" syntax
        this.wishes.splice(this.wishes.indexOf(wish), 1)
      },

      editWish: function (wish) {
        this.beforeEditCache = wish.title
        this.editedWish = wish
      },

      doneEdit: function (wish) {
        if (!this.editedWish) {
          return
        }

        this.editedWish = null
        wish.title = wish.title.trim()

        if (!wish.title) {
          this.removeWish(wish)
        }
      },

      cancelEdit: function (wish) {
        this.editedWish = null
        wish.title = this.beforeEditCache
      },

      removeCompleted: function () {
        this.wishes = filters.active(this.wishes)
      },

      handleErrorClick: function () {
        this.error = null;
      },
    },

    // a custom directive to wait for the DOM to be updated
    // before focusing on the input field.
    // http://vuejs.org/guide/custom-directive.html
    directives: {
      'wish-focus': function (el, binding) {
        if (binding.value) {
          el.focus()
        }
      }
    }
  }

  export default Wishes

</script>

<style>
  [v-cloak] { display: none; }
</style>
