function Tile(sl1, sl2) {
    this.sl1 = sl1;
    this.sl2 = sl2;

    this.carroca = function (){
      if (this.sl1==this.sl2){
        return true;
      }else {
        return false;
      }
    }
}
