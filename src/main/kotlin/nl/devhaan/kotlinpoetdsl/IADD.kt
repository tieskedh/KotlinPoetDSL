package nl.devhaan.kotlinpoetdsl/*
* Due to the weird syntaxis, there are more than one building functions
* These functions can be called after each other.
* this is for adding the files to the surrounding context.
*/

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
    fun unregisterBuilder(builder: IBuilder)
}