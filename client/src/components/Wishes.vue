<template>
    <div>
        <h1 class="title">Wishes</h1>
        <section class="wishlistapp">
            <div v-if="loading">
                <h1 class = "loading">Loading...</h1>
            </div>
            <div v-else>
                <header class="header">
                    <input class="new-wish"
                        autofocus autocomplete="off"
                        :placeholder="this.inputPlaceholder"
                        v-model="newWish"
                        @keyup.enter="addWish">
                </header>                
            </div>
        </section>        
    </div>
</template>

