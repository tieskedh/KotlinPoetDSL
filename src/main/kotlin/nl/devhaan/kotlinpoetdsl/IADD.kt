package nl.devhaan.kotlinpoetdsl/*
* Due to the weird syntaxis, there are more than one returning functions.
* Because these returning functions often are infix functions, the funtio

this is for adding the files to the surrounding context. */

interface IBuilder{
    /**
     * Should build and add to context
     * It's possible that the code will be build more often.
     * This is due to the weird syntaxis.
     * because the code
     */
    fun finish()
}

interface IAcceptor{
    /**
     *
     */
    fun registerBuilder(builder: IBuilder)
}